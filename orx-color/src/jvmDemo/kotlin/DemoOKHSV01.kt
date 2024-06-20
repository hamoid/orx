import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.color.spaces.ColorOKHSLa
import org.openrndr.extra.color.spaces.ColorOKHSVa

fun main() = application {
    configure {
        height = 150
    }
    program {
        extend {
            val c = ColorRGBa.GREEN
            val okhsv = ColorOKHSVa.fromColorRGBa(c)
            val hsv = c.toHSVa()
            val hsl = c.toHSLa()
            val okhsl = ColorOKHSLa.fromColorRGBa(c)

            drawer.translate(10.0, 0.0)

            for (i in 0 until 36) {
                drawer.fill = okhsv.shiftHue(i * 10.0).saturate(1.0).toRGBa()
                drawer.rectangle(i * 15.0, 10.0, 15.0, 25.0)
                drawer.fill = hsv.shiftHue(i * 10.0).saturate(1.0).toRGBa()
                drawer.rectangle(i * 15.0, 40.0, 15.0, 25.0)

                drawer.fill = okhsl.shiftHue(i * 10.0).saturate(1.0).toRGBa()
                drawer.rectangle(i * 15.0, 70.0, 15.0, 25.0)
                drawer.fill = hsl.shiftHue(i * 10.0).saturate(1.0).toRGBa()
                drawer.rectangle(i * 15.0, 100.0, 15.0, 25.0)

            }
        }
    }
}