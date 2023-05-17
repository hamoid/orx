import org.openrndr.WindowMultisample
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.DrawPrimitive
import org.openrndr.draw.shadeStyle
import org.openrndr.extra.camera.Orbital
import org.openrndr.extra.meshgenerators.buildTriangleMesh
import org.openrndr.extra.meshgenerators.extrudeContourSteps
import org.openrndr.math.Vector3
import org.openrndr.shape.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.random.Random

private fun ShapeContour.toPath3D() = Path3D(segments.map { seg ->
    Segment3D(
        seg.start.xy0,
        seg.control.map { it.xy0 }.toTypedArray(),
        seg.end.xy0
    )
}, closed)

private fun r() = Vector3(
    Random.nextDouble(-4.0, 4.0),
    Random.nextDouble(-4.0, 4.0),
    Random.nextDouble(-4.0, 4.0)
)

fun main() {
    application {
        configure {
            width = 800
            height = 800
            multisample = WindowMultisample.SampleCount(8)
        }
        program {
            val m = buildTriangleMesh {
                color = ColorRGBa.PINK

                val beziers = List(4) {
                    Segment3D(r(), r(), r(), r())
                }

                translate(-1.0, 0.0, 0.0)

                val crossSection = Circle(0.0, 0.0, 0.2).contour

                for (i in 0 until 20) {
                    val t = i / (20.0 - 1.0)
                    val path = Path3D(
                        listOf(
                            Segment3D(
                                beziers[0].position(t),
                                beziers[1].position(t),
                                beziers[2].position(t),
                                beziers[3].position(t)
                            )
                        ), false
                    )
                    extrudeContourSteps(
                        crossSection,
                        path,
                        120,
                        Vector3.UNIT_Y,
                        contourDistanceTolerance = 0.02,
                        pathDistanceTolerance = 0.001,
                        // env = { t: Double -> 0.5 - 0.5 * cos(t * 2 * PI)  }
                    )
                }
            }

            extend(Orbital()) {
                this.eye = Vector3(0.0, 3.0, 7.0)
                this.lookAt = Vector3(0.0, 0.0, 0.0)
            }

            extend {
                drawer.shadeStyle = shadeStyle {
                    fragmentTransform = """
                        x_fill = va_color;
                        x_fill.rgb *= v_viewNormal.z;
                    """.trimIndent()
                }

                drawer.vertexBuffer(m, DrawPrimitive.TRIANGLES)
            }
        }
    }
}
