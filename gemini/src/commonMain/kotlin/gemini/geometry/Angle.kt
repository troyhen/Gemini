package gemini.geometry

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

@JvmInline
value class Angle(val degrees: Float = 0f) {
    val radians: Float get() = degrees * DEGREES_TO_RADIANS
}

const val PI = Math.PI.toFloat()
const val PI2 = PI * 2
const val DEGREES_TO_RADIANS = PI / 180
const val RADIANS_TO_DEGREES = 180 / PI

val Double.radians get() = Angle(toFloat() * RADIANS_TO_DEGREES)
val Float.radians get() = Angle(this * RADIANS_TO_DEGREES)
val Int.radians get() = Angle(this * RADIANS_TO_DEGREES)
val Long.radians get() = Angle(this * RADIANS_TO_DEGREES)
val Double.degrees get() = Angle(toFloat())
val Float.degrees get() = Angle(this)
val Int.degrees get() = Angle(toFloat())
val Long.degrees get() = Angle(toFloat())

operator fun Angle.minus(other: Angle): Angle = Angle(degrees - other.degrees)
operator fun Angle.minus(other: Float): Angle = Angle(degrees - other)
operator fun Angle.plus(other: Angle): Angle = Angle(degrees + other.degrees)
operator fun Angle.plus(other: Float): Angle = Angle(degrees + other)
operator fun Angle.times(other: Float): Angle = Angle(degrees * other)
operator fun Angle.times(other: Int): Angle = Angle(degrees * other)
operator fun Angle.div(other: Float): Angle = Angle(degrees / other)
operator fun Angle.div(other: Int): Angle = Angle(degrees / other)

fun Angle.cos(): Float = cos(radians)
fun Angle.sin(): Float = sin(radians)
fun Angle.tan(): Float = tan(radians)
