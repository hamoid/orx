import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.fcurve.FCurve
import org.openrndr.extra.fcurve.fcurve
import org.openrndr.extra.noise.Random
import org.openrndr.math.Vector2

fun make0To1FCurve(steps: Int): FCurve {
    val xValues = List(steps) {
        Random.double(0.01, 1.0)
    }.sorted()

    val yValues = listOf(0.0) + List(steps - 2) {
        Random.double(0.1, 0.9)
    }.sorted() + 1.0

    val lines = xValues.mapIndexed { i, d ->
        "T$d,${yValues[i]}"
    }.joinToString(" ")

    val f = "M0 L0,0 $lines"

    // println(f.replace(" ", "\n"))
    return fcurve(f)
}

fun main() {
    application {
        program {
            Random.seed = "6789"

            // Go from 0.0 to 1.0 at varying speed in the specified time

            val xCurve = make0To1FCurve(10)
            val xValue = xCurve.sampler(true)
            val duration = 20.0

            extend {
                drawer.stroke = ColorRGBa.PINK
                drawer.contours(xCurve.contours(Vector2(width / xCurve.duration, height * 1.0)))
                drawer.circle(xValue(seconds / duration) * width, height * 0.5, 20.0)
            }
        }
    }
}
