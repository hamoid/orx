import junit.framework.Assert.assertTrue
import org.openrndr.extra.noise.Random
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.math.abs

object TestRandom : Spek({
    describe("Random.sign()") {
        it("is always -1.0 or 1.0") {
            assertTrue("invalid value found",
                    List(50) { Random.sign() }.all { abs(it) == 1.0 })
        }
    }
})