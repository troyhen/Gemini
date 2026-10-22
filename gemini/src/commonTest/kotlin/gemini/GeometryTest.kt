package gemini

import androidx.compose.ui.geometry.Offset
import gemini.geometry.*
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GeometryTest {

    private fun assertFloatEquals(expected: Float, actual: Float, tolerance: Float = 1e-4f) {
        assertTrue(
            abs(expected - actual) <= tolerance,
            "Expected $expected (+/- $tolerance), but was $actual",
        )
    }

    @Test
    fun angleTrigAndArithmetic() {
        assertFloatEquals(0f, 0f.degrees.sin())
        assertFloatEquals(1f, 0f.degrees.cos())
        assertFloatEquals(1f, 90f.degrees.sin())
        assertFloatEquals(0f, 90f.degrees.cos())
        assertFloatEquals(45f, (90f.degrees / 2).degrees)
        assertFloatEquals(135f, (90f.degrees + 45f.degrees).degrees)
    }

    @Test
    fun locationOperationsAndContentEquals() {
        val loc1 = Location(1f, 2f, 3f)
        val loc2 = Location(1f, 2f, 3f)
        assertTrue(loc1.contentEquals(loc2))

        loc1.move(2f, -1f, 4f)
        assertFloatEquals(3f, loc1.x)
        assertFloatEquals(1f, loc1.y)
        assertFloatEquals(7f, loc1.z)
        assertFalse(loc1.contentEquals(loc2))

        val velocity = Velocity(10f, -20f, 5f)
        loc2.change(velocity, 500.milliseconds)
        assertFloatEquals(6f, loc2.x)
        assertFloatEquals(-8f, loc2.y)
        assertFloatEquals(5.5f, loc2.z)
    }

    @Test
    fun zeroAndOneConstantsReturnFreshInstances() {
        val zeroLoc = Location.Zero
        zeroLoc.set(99f, 99f, 99f)
        assertFloatEquals(0f, Location.Zero.x)

        val oneScale = Scale.ONE
        oneScale.x = 5f
        assertFloatEquals(1f, Scale.ONE.x)

        val zeroRect = Rectangle.Zero
        zeroRect.left = 42f
        assertFloatEquals(0f, Rectangle.Zero.left)

        val zeroIntRect = IntRectangle.Zero
        zeroIntRect.left = 42
        assertEquals(0, IntRectangle.Zero.left)
    }

    @Test
    fun rotationOrderAndAccumulation() {
        val rot = Rotation()
        rot.rotate(r = 15f.degrees, p = 30f.degrees, y = 45f.degrees)
        assertFloatEquals(15f, rot.r.degrees)
        assertFloatEquals(30f, rot.p.degrees)
        assertFloatEquals(45f, rot.y.degrees)

        rot.rotate(r = 10f.degrees, p = (-5f).degrees, y = 15f.degrees)
        assertFloatEquals(25f, rot.r.degrees)
        assertFloatEquals(25f, rot.p.degrees)
        assertFloatEquals(60f, rot.y.degrees)
    }

    @Test
    fun velocityInPlaceMutations() {
        val v = Velocity(4f, -6f, 2f)
        v.negate()
        assertFloatEquals(-4f, v.xs)
        assertFloatEquals(6f, v.ys)
        assertFloatEquals(-2f, v.zs)

        v *= 0.5f
        assertFloatEquals(-2f, v.xs)
        assertFloatEquals(3f, v.ys)
        assertFloatEquals(-1f, v.zs)

        val accel = Velocity(2f, -4f, 6f)
        v.change(accel, 1.seconds)
        assertFloatEquals(0f, v.xs)
        assertFloatEquals(-1f, v.ys)
        assertFloatEquals(5f, v.zs)
    }

    @Test
    fun rectangleOverlapAndContains() {
        val r1 = Rectangle(left = 0f, top = 0f, right = 10f, bottom = 10f)
        val r2 = Rectangle(left = 5f, top = 5f, right = 15f, bottom = 15f)
        val r3 = Rectangle(left = 20f, top = 20f, right = 30f, bottom = 30f)

        assertTrue(r1.overlaps(r2))
        assertTrue(r2.overlaps(r1))
        assertFalse(r1.overlaps(r3))
        assertTrue(r1.contains(Offset(5f, 5f)))
        assertFalse(r1.contains(Offset(15f, 15f)))
    }

    @Test
    fun positionRectangleRespectsPivot() {
        val rect = Rectangle()
        val centerPos = Position(
            location = Location(100f, 200f),
            space = Space(40f, 20f),
            pivot = Pivot.Center,
        )
        centerPos.rectangle(rect)
        assertFloatEquals(80f, rect.left)
        assertFloatEquals(190f, rect.top)
        assertFloatEquals(120f, rect.right)
        assertFloatEquals(210f, rect.bottom)

        val nwPos = Position(
            location = Location(100f, 200f),
            space = Space(40f, 20f),
            pivot = Pivot.NorthWest,
        )
        nwPos.rectangle(rect)
        assertFloatEquals(100f, rect.left)
        assertFloatEquals(200f, rect.top)
        assertFloatEquals(140f, rect.right)
        assertFloatEquals(220f, rect.bottom)

        val sePos = Position(
            location = Location(100f, 200f),
            space = Space(40f, 20f),
            pivot = Pivot.SouthEast,
        )
        sePos.rectangle(rect)
        assertFloatEquals(60f, rect.left)
        assertFloatEquals(180f, rect.top)
        assertFloatEquals(100f, rect.right)
        assertFloatEquals(200f, rect.bottom)
    }

    @Test
    fun remapAndDistance() {
        assertFloatEquals(50f, 0.5f.remap(0f, 1f, 0f, 100f))
        assertFloatEquals(-10f, (-1f).remap(-1f, 1f, -10f, 10f))
        assertFloatEquals(0f, 0f.remap(-1f, 1f, -10f, 10f))

        val a = Location(0f, 0f, 0f)
        val b = Location(3f, 4f, 0f)
        assertFloatEquals(25f, distanceSquared(a, b))
        assertFloatEquals(5f, distance(a, b))
    }
}
