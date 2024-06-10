import org.openrndr.application
import org.openrndr.extensions.SingleScreenshot
import org.openrndr.extra.keyframer.Keyframer
import org.openrndr.resourceUrl
import java.net.URI

fun main() = application {
    program {
        class Animation : Keyframer() {
            val position by Vector2Channel(arrayOf("x", "y"))
            val radius by DoubleChannel("x")
        }

        val animation = Animation()
        animation.loadFromJson(
            URI(resourceUrl("/demo-simple-expressions-01.json")).toURL(),
                parameters = mapOf("cycleDuration" to 2.0))
        if (System.getProperty("takeScreenshot") == "true") {
            extend(SingleScreenshot()) {
                this.outputFile = System.getProperty("screenshotPath")
            }
        }
        extend {
            animation(seconds)
            drawer.circle(animation.position, animation.radius)
        }
    }
}