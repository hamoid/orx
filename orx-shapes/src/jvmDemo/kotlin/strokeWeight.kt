import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import kotlin.math.pow

fun main() = application {
    program {
        val c = Circle(Vector2.ZERO, 200.0).contour
        extend {
            drawer.translate(drawer.bounds.center)
            drawer.fill = null
            drawer.stroke = ColorRGBa.WHITE
            drawer.contour(c)

            repeat(6) {
                drawer.stroke = ColorRGBa.WHITE.opacify(0.2)
                drawer.strokeWeight = 2.0.pow(it)
                drawer.segments(c.sub((it * 131.37) % 1.0, (it * 93.54) % 1.0).segments)
            }
        }
    }
}