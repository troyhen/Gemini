package gemini.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.*
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

val Duration.seconds: Float get() = inWholeMicroseconds * 1e-6f
val Float.seconds: Duration get() = this.toDouble().seconds

val Angle.random: Angle get() = (Random.nextFloat() * degrees).degrees
val Double.random: Double get() = Random.nextDouble() * this
val Duration.random: Duration get() = seconds.random.seconds
val Float.random: Float get() = Random.nextFloat() * this
val Int.random: Int get() = Random.nextInt(this)
val Long.random: Long get() = Random.nextLong(this)
val Offset.random: Offset get() = Offset(x.random, y.random)
val Size.random: Size get() = Size(width.random, height.random)

fun Angle.randomPlus(plus: Angle = this / -2): Angle = (Random.nextFloat() * degrees).degrees + plus
fun Double.randomPlus(plus: Double = this / -2): Double = Random.nextDouble() * this + plus
fun Duration.randomPlus(plus: Duration = this / -2): Duration = seconds.random.seconds + plus
fun Float.randomPlus(plus: Float = this / -2): Float = Random.nextFloat() * this + plus
fun Int.randomPlus(plus: Int = this / -2): Int = Random.nextInt(this) + plus
fun Long.randomPlus(plus: Long = this / -2): Long = Random.nextLong(this) + plus
fun Offset.randomPlus(plus: Offset = this / -2): Offset = Offset(x.random, y.random) + plus
fun Size.randomPlus(plus: Size = this / -2): Size = Size(width.random, height.random) + plus

infix fun Offset.rotate(angle: Angle): Offset {
    val r = angle.radians
    val c = cos(r)
    val s = sin(r)
    return Offset(x * c - y * s, y * c + x * s)
}

operator fun Offset.div(scale: Int): Offset = Offset(x / scale, y / scale)
operator fun Offset.times(scale: Int): Offset = Offset(x * scale, y * scale)
operator fun Offset.times(size: Size): Offset = Offset(x * size.width, y * size.height)
val IntOffset.max get() = max(x, y)
val IntOffset.min get() = min(x, y)
val Offset.max get() = max(x, y)
val Offset.min get() = min(x, y)
operator fun Size.div(scale: Scale): Size = Size(width / scale.x, height / scale.y)
operator fun Size.div(scale: Int): Size = Size(width / scale, height / scale)
val IntSize.max get() = max(width, height)
val IntSize.min get() = min(width, height)
val Size.max get() = max(width, height)
val Size.min get() = min(width, height)
operator fun Size.minus(plus: Size): Size = Size(width - plus.width, height - plus.height)
operator fun Size.plus(plus: Size): Size = Size(width + plus.width, height + plus.height)
operator fun Size.times(scale: Scale): Size = Size(width * scale.x, height * scale.y)
operator fun Size.times(scale: Int): Size = Size(width * scale, height * scale)
fun Size.toOffset(): Offset = Offset(width, height)

/**
 * Map the value from one range to another
 */
fun Float.remap(start1: Float, stop1: Float, start2: Float, stop2: Float, coerceIn: Boolean = false): Float {
    val result = (this - start1) / (stop1 - start1) * (stop2 - start2) + start2
    return if (coerceIn) result.coerceIn(start2, stop2) else result
}

/**
 * Map the [Offset] from one range to a rectangle bounded by 0, 0 to [Size]
 */
fun Offset.remap(size: Size, start: Float = -1f, stop: Float = 1f, coerceIn: Boolean = false): Offset {
    return Offset(x.remap(start, stop, 0f, size.width, coerceIn), y.remap(start, stop, 0f, size.height, coerceIn))
}

/**
 * Does the 3D transform on [from] and returns the result in [to]
 */
fun Matrix.map(from: Location, to: Location = Location()): Location {
    val x = from.x
    val y = from.y
    val z = from.z
    to.z = this[0, 3] * x + this[1, 3] * y + z * this[2, 3] + this[3, 3]
    val inverseZ = 1 / to.z
    val pZ = if (inverseZ.isFinite()) inverseZ else 0f
    to.x = pZ * (this[0, 0] * x + this[1, 0] * y + z * this[2, 0] + this[3, 0])
    to.y = pZ * (this[0, 1] * x + this[1, 1] * y + z * this[2, 1] + this[3, 1])
    return to
}

fun Matrix.perspective(left: Float = -1f, right: Float = 1f, top: Float = -1f, bottom: Float = 1f, near: Float = -1f, far: Float = 1f) {
    val n2 = 2 * near
    val rml = right - left
    val bmt = bottom - top
    val fmn = far - near
    reset()
    set(0, 0, n2 / rml)
    set(0, 3, -near * (right + left) / rml)
    set(1, 1, n2 / bmt)
    set(1, 3, -near * (bottom + top) / bmt)
    set(2, 2, (far + near) / fmn)
    set(2, 3, -far * n2 / fmn)
    set(3, 2, 1f)
    set(3, 3, 0f)
    println("perspective\n$this")
}

fun perspectiveMatrix(left: Float = -1f, right: Float = 1f, top: Float = -1f, bottom: Float = 1f, near: Float = -1f, far: Float = 1f): Matrix {
    return Matrix().apply {
        perspective(left, right, top, bottom, near, far)
    }
}

fun Matrix.orthographic(left: Float = -1f, right: Float = 1f, top: Float = -1f, bottom: Float = 1f, near: Float = -1f, far: Float = 1f) {
    val width = right - left
    val height = bottom - top
    val depth = far - near
    reset()
    set(0, 0, 2 / width)
    set(0, 3, -(right + left) / width)
    set(1, 1, 2 / height)
    set(1, 3, -(bottom + top) / height)
    set(2, 2, 2 / depth)
    set(2, 3, -(far + near) / depth)
    println("orthographic\n$this")
}

fun orthographicMatrix(left: Float = -1f, right: Float = 1f, top: Float = -1f, bottom: Float = 1f, near: Float = -1f, far: Float = 1f): Matrix {
    return Matrix().apply {
        orthographic(left, right, top, bottom, near, far)
    }
}

operator fun IntOffset.times(size: IntSize): IntOffset = IntOffset(x * size.width, y * size.height)
inline fun Size.round(): IntSize = IntSize(width.roundToInt(), height.roundToInt())
