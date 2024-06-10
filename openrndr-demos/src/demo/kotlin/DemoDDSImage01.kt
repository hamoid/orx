import org.openrndr.application
import org.openrndr.draw.loadImage

fun main() {
    application {
        program {
            val image = loadImage("demo-data/cubemaps/garage_iem.dds")
            println(image.format)
            println(image.type)
            extend {
                drawer.image(image)
            }
        }
    }
}