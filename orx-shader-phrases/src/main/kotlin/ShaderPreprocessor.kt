package org.openrndr.extra.shaderphrases

import mu.KotlinLogging
import org.openrndr.draw.Shader
import org.openrndr.draw.codeFromURL
import org.openrndr.extra.shaderphrases.annotations.ShaderPhrases
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

private val logger = KotlinLogging.logger {}

/**
 * A single shader phrase.
 */
class ShaderPhrase(val phrase: String) {
    /**
     * Register this shader phrase in the [ShaderPhraseRegistry]
     * This will likely be called by [ShaderPhraseBook]
     */
    fun register(bookId: String? = null) {
        val functionRex =
            Regex("(float|int|[bi]?vec2|[bi]?vec3|[bi]?vec4|mat3|mat4)[ ]+([a-zA-Z0-9_]+)[ ]*\\(.*\\).*")
        val defs = phrase.split("\n").filter {
            functionRex.matches(it)
        }.take(1).mapNotNull {
            val m = functionRex.find(it)
            m?.groupValues?.getOrNull(2)
        }
        val id = defs.firstOrNull() ?: error("no function body found in phrase")
        ShaderPhraseRegistry.registerPhrase("${bookId?.let { "$it." } ?: ""}$id", this)
    }
}
/**
 * A book of shader phrases.
 */
open class ShaderPhraseBook(val bookId: String) {
    private var registered = false

    /**
     * Registers all known shader phrases
     */
    fun register() {
        if (!registered) {
            this::class.declaredMemberProperties.filter {
                it.returnType.toString() == "org.openrndr.extra.shaderphrases.ShaderPhrase"
            }.map {
                @Suppress("UNCHECKED_CAST")
                val m = it as? KProperty1<ShaderPhraseBook, ShaderPhrase>
                m?.get(this)?.register(bookId)
            }
            registered = true
        }
    }
}

/**
 * The global, application-wide, shader phrase registry
 */
object ShaderPhraseRegistry {
    private val phrases = mutableMapOf<String, ShaderPhrase>()
    /**
     * Registers a [phrase] with [id]
     */
    fun registerPhrase(id: String, phrase: ShaderPhrase) {
        phrases[id] = phrase
    }

    /**
     * Finds a phrase for [id], returns null when no phrase found
     */
    fun findPhrase(id: String): ShaderPhrase? {
        val phrase = phrases[id]
        if (phrase == null) {
            logger.warn { "no phrase found for id: \"$id\"" }
        }
        return phrase
    }
}

/**
 * Preprocess shader source.
 * Looks for "#pragma import" statements and injects found phrases.
 * @param source GLSL source code encoded as string
 * @return GLSL source code with injected shader phrases
 */
fun preprocessShader(source: String, symbols: Set<String> = emptySet()): String {
    val newSymbols = mutableSetOf<String>()
    newSymbols.addAll(symbols)

    val lines = source.split("\n")
    val processed = lines.mapIndexed { index, it ->
        if (it.startsWith("#pragma import")) {
            val tokens = it.split(" ")
            val symbol = tokens[2].trim().replace(";", "")
            val fullTokens = symbol.split(".")
            val fieldName = fullTokens.last().replace(";", "").trim()
            val packageClassTokens = fullTokens.dropLast(1)
            val packageClass = packageClassTokens.joinToString(".")

            if (symbol !in newSymbols) {
                newSymbols.add(symbol)
                val registryPhrase = ShaderPhraseRegistry.findPhrase(symbol)

                registryPhrase?.let { preprocessShader(it.phrase, newSymbols) }
                    ?: try {
                        /*  Note that JVM-style reflection is used here because of short-comings in the Kotlin reflection
                                            library (as of 1.3.61), most notably reflection support for file facades is missing. */
                        val c = Class.forName(packageClass)
                        if (c.annotations.any { it.annotationClass == ShaderPhrases::class }) {
                            if (fieldName == "*") {
                                c.declaredMethods.filter { it.returnType.name == "java.lang.String" }.map {
                                    "/* imported from $packageClass.$it */\n${it.invoke(null)}\n"
                                }.joinToString("\n") +
                                        c.declaredFields.filter { it.type.name == "java.lang.String" }.map {
                                            "/* imported from $packageClass.$it */\n${it.get(null)}\n"
                                        }.joinToString("\n")
                            } else {
                                var result: String?
                                try {
                                    val methodName = "get${fieldName.take(1).toUpperCase() + fieldName.drop(1)}"
                                    result =
                                        preprocessShader(c.getMethod(methodName).invoke(null) as String, newSymbols)
                                } catch (e: NoSuchMethodException) {
                                    try {
                                        result =
                                            preprocessShader(
                                                c.getDeclaredField(fieldName).get(null) as String,
                                                newSymbols
                                            )
                                    } catch (e: NoSuchFieldException) {
                                        println(source)
                                        error("field \"$fieldName\" not found in \"#pragma import $packageClass.$fieldName\" on line ${index + 1}")
                                    }
                                }
                                result
                            }
                        } else {
                            throw IllegalArgumentException("class $packageClass has no ShaderPhrases annotation")
                        }
                    } catch (e: ClassNotFoundException) {
                        println(source)
                        error("class \"$packageClass\" not found in \"#pragma import $packageClass\" on line ${index + 1}")
                    }
            } else {
                ""
            }
        } else {
            it
        }
    }
    return processed.joinToString("\n")
}

fun String.preprocess() = preprocessShader(this)


/**
 * Preprocess shader source from url
 * Looks for "#pragma import" statements and injects found phrases.
 * @param url url pointing to GLSL shader source
 * @return GLSL source code with injected shader phrases
 */
fun preprocessShaderFromUrl(url: String, symbols: Set<String> = emptySet()): String {
    return preprocessShader(codeFromURL(url), symbols)
}

fun Shader.Companion.preprocessedFromUrls(
    vsUrl: String,
    tcsUrl: String? = null,
    tesUrl: String? = null,
    gsUrl: String? = null,
    fsUrl: String
): Shader {

    val vsCode = codeFromURL(vsUrl).preprocess()
    val tcsCode = tcsUrl?.let { codeFromURL(it) }?.preprocess()
    val tesCode = tesUrl?.let { codeFromURL(it) }?.preprocess()
    val gsCode = gsUrl?.let { codeFromURL(it) }?.preprocess()
    val fsCode = codeFromURL(fsUrl).preprocess()
    val name = "$$vsUrl / $gsUrl / $fsUrl"
    return Shader.createFromCode(vsCode, tcsCode, tesCode, gsCode, fsCode, name)
}

