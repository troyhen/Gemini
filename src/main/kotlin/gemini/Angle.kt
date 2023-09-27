package gemini

@JvmInline
value class Angle(val value: Float = 0f)

const val PI = Math.PI.toFloat()

val Double.degrees get() = Angle(toFloat() * PI / 180)
val Float.degrees get() = Angle(this * PI / 180)
val Int.degrees get() = Angle(this * PI / 180)
val Long.degrees get() = Angle(this * PI / 180)
val Double.radians get() = Angle(toFloat())
val Float.radians get() = Angle(this)
val Int.radians get() = Angle(toFloat())
val Long.radians get() = Angle(toFloat())

operator fun Angle.minus(other: Angle): Angle = Angle(value - other.value)
operator fun Angle.minus(radians: Float): Angle = Angle(value - radians)
operator fun Angle.plus(other: Angle): Angle = Angle(value + other.value)
operator fun Angle.plus(radians: Float): Angle = Angle(value + radians)
operator fun Angle.times(other: Float): Angle = Angle(value * other)
operator fun Angle.div(other: Float): Angle = Angle(value / other)
