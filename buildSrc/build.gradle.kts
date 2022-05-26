// buildSrc/build.gradle.kts

plugins {
    `kotlin-dsl`
}

apply(from = "../dependencies.gradle.kts")

sourceSets {
    val preload by creating {
        this.java {
            srcDir("src/preload/kotlin")
        }
    }
    val main by getting {
    }

}

repositories {
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}


val openrndrVersion = ext.get("openrndrVersion")

println("+++")
println(ext["openrndrVersion"])

// DONE: I figured out how to share data between
// buildSrc, the root project and the subprojects.

// TODO: make orx-multiplatform.gradle.kts configurable

// Figure out how to pass variables from a subproject
// into the plugin

// I created a precompiled plugin called orx-multiplatform
// But so far it is only used in orx-color.
// To use it in other places it should be configurable,
// so I can pass variables to specify the differences when
// used in various subprojects.
// I could pass lists of dependencies needed in each one.
// There's a doc in gradle about configuring the pre-compiled
// plugin.
// I also need to know how to make subprojects unique.
// What if a subprojects has unique requirements?

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.6.20")
    val preloadImplementation by configurations.getting {  }
    preloadImplementation("org.openrndr:openrndr-application:$openrndrVersion")
    preloadImplementation("org.openrndr:openrndr-extensions:$openrndrVersion")
}

tasks.getByName("compileKotlin").dependsOn("compilePreloadKotlin")