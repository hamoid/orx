package rectify

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.draw.*
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.color.mixing.mixSpectral
import org.openrndr.extra.color.presets.ORANGE
import org.openrndr.extra.fx.blend.BlendSpectral
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.shapes.hobbycurve.hobbyCurve
import org.openrndr.extra.shapes.rectify.rectified
import org.openrndr.extra.shapes.utilities.fromContours
import org.openrndr.namedTimestamp
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.bounds

fun main() {
    application {
        configure {
            width = 2000
            height = 2000
        }
        program {
            val rt = renderTarget(width, height) {
                colorBuffer(type = ColorType.FLOAT32)
                depthBuffer()
            }
            val result = rt.colorBuffer(0).createEquivalent()

            Random.seed = namedTimestamp()

            val cA = ColorRGBa.ORANGE.toHSLa().shiftHue(Random.double0(360.0)).toRGBa()
            val cB = cA.toHSLa().shiftHue(180.0).toRGBa().shade(0.8)

            val c0 = hobbyCurve(drawer.bounds.scatter(300.0, distanceToEdge = 400.0), true)
            val c1 = hobbyCurve(drawer.bounds.scatter(200.0, distanceToEdge = 400.0), true)

            val r0 = c0.rectified()
            val r1 = c1.rectified()

            val t0s = (0 until c0.segments.size + 1).map { it.toDouble() / c0.segments.size }
            val t1s = (0 until c1.segments.size + 1).map { it.toDouble() / c1.segments.size }

            val rt0s = t0s.map { r0.inverseRectify(it) }
            val rt1s = t1s.map { r1.inverseRectify(it) }

            val a = ShapeContour.fromContours(r0.splitAt(rt1s), false)
            val b = ShapeContour.fromContours(r1.splitAt(rt0s), false)

            fun ShapeContour.mix(other: ShapeContour, factor: Double): ShapeContour {
                val segs = this.segments.zip(other.segments).map {
                    it.first * (1.0 - factor) + it.second * factor
                }
                return ShapeContour.fromSegments(segs, false)
            }

            val blendSpectral = BlendSpectral().also {
                it.fill = 0.5
            }

            val n = 100
            repeat(n) {
                drawer.isolatedWithTarget(rt) {
                    clear(ColorRGBa.TRANSPARENT)
                    translate(
                        drawer.bounds.center -
                                listOf(a.bounds, b.bounds).bounds.center
                    )
                    strokeWeight = 4.0
                    stroke = mixSpectral(cA, cB, it / (n - 1.0)).toSRGB()
                    lineJoin = LineJoin.ROUND
                    lineCap = LineCap.ROUND
                    contour(a.mix(b, it / (n - 1.0)))
                }
                blendSpectral.apply(rt.colorBuffer(0), result, result)
            }

            extend(Screenshots())
            extend {
                drawer.clear(rgb(0.1))
                drawer.image(result)
            }
        }
    }
}