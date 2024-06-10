import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.LineCap
import org.openrndr.draw.LineJoin
import org.openrndr.draw.isolated
import org.openrndr.extra.color.presets.ORANGE
import org.openrndr.extra.shapes.adjust.adjustContour
import org.openrndr.extra.shapes.primitives.regularStar
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import org.openrndr.shape.Segment2D
import org.openrndr.shape.ShapeContour

fun main() = application {
    configure {
        width = 800
        height = 800
    }
    program {
        val withGaps = List(3) {
            val a = it * 120.0
            val r = 100.0 //+ it * 30.0
            Segment2D(
                Polar(a, r).cartesian,
                Polar(a + 50.0, r * 0.3).cartesian,
                Polar(a + 100.0, r).cartesian
            )
        }
        val noGaps = withGaps.fillGaps(true)

        val starGaps = adjustContour(regularStar(5, 180.0, 350.0, Vector2.ZERO)) {
            selectEdges { _ -> true }
            edges.forEach {
                it.rotate(20.0)
            }
        }.segments.map { it.sub(0.1, 0.8) }
        val starNoGaps = starGaps.fillGaps(true)

        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.translate(drawer.bounds.center)
            drawer.fill = null
            drawer.lineJoin = LineJoin.ROUND
            drawer.lineCap = LineCap.ROUND

            drawer.isolated {
                stroke = ColorRGBa.ORANGE
                strokeWeight = 5.0
                segments(withGaps)
                segments(starGaps)
            }

            drawer.contour(noGaps)
            drawer.contour(starNoGaps)
        }
    }
}

private fun List<Segment2D>.fillGaps(closed: Boolean): ShapeContour {
    val result = mutableListOf<Segment2D>()
    val pairs = (if(closed) this + this.first() else this).zipWithNext()
    pairs.forEach { (a, b) ->
        result.add(a)
        if (a.end.squaredDistanceTo(b.start) > 1E-6) {
            // TODO: maybe use the dot product between
            // the directions.
            val d = a.end.distanceTo(b.start) * 0.5
            val gap = Segment2D(
                a.end,
                a.end + a.direction(1.0) * d,
                b.start - b.direction(0.0) * d,
                b.start
            )
            result.add(gap)
        }
    }
    if(!closed) result.add(this.last())
    return ShapeContour(result, closed)
}
