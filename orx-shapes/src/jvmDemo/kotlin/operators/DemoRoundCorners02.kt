package operators

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.shapes.adjust.adjustContour
import org.openrndr.extra.shapes.operators.roundCorners
import org.openrndr.shape.Rectangle

fun main() = application {
    program {
        val rp = adjustContour(Rectangle.fromCenter(drawer.bounds.center, 250.0).contour) {
            selectVertices(0)
            vertex.rotate(30.0)

        }.roundCorners(15.0)
        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.contour(rp)
        }
    }
}