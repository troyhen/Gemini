package gemini

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import gemini.geometry.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

inline fun <E> Array<E>.fastForEach(from: Int = 0, block: (E) -> Unit) {
    for (index in from until size) {
        block(get(index))
    }
}

inline fun <E> Array<E>.fastForEachIndexed(block: (Int, E) -> Unit) {
    repeat(size) { index ->
        block(index, get(index))
    }
}

inline fun <E> List<E>.fastForEach(from: Int = 0, block: (E) -> Unit) {
    for (index in from until size) {
        block(get(index))
    }
}

inline fun <E> List<E>.fastForEachIndexed(block: (Int, E) -> Unit) {
    repeat(size) { index ->
        block(index, get(index))
    }
}

val Duration.inSeconds: Float get() = inWholeMicroseconds * 1e-6f

val Angle.random: Angle get() = (Random.nextFloat() * degrees).degrees
val Double.random: Double get() = Random.nextDouble() * this
val Duration.random: Duration get() = inSeconds.random.seconds
val Float.random: Float get() = Random.nextFloat() * this
val Int.random: Int get() = Random.nextInt(this)
val Long.random: Long get() = Random.nextLong(this)
val Offset.random: Offset get() = Offset(x.random, y.random)
val Size.random: Size get() = Size(width.random, height.random)
fun Angle.randomPlus(plus: Angle = this / -2): Angle = (Random.nextFloat() * degrees).degrees + plus
fun Double.randomPlus(plus: Double = this / -2): Double = Random.nextDouble() * this + plus
fun Duration.randomPlus(plus: Duration = this / -2): Duration = inSeconds.random.seconds + plus
fun Float.randomPlus(plus: Float = this / -2): Float = Random.nextFloat() * this + plus
fun Int.randomPlus(plus: Int = this / -2): Int = Random.nextInt(this) + plus
fun Long.randomPlus(plus: Long = this / -2): Long = Random.nextLong(this) + plus
fun Offset.randomPlus(plus: Offset = this / -2): Offset = Offset(x.random, y.random) + plus
fun Size.randomPlus(plus: Size = this / -2): Size = Size(width.random, height.random) + plus

fun Offset.rotate(angle: Angle): Offset {
    val c = cos(angle.radians)
    val s = sin(angle.radians)
    return Offset(x * c - y * s, y * c + x * s)
}

val Float.seconds: Duration get() = this.toDouble().seconds
operator fun Offset.div(scale: Int): Offset = Offset(x / scale, y / scale)
operator fun Offset.times(scale: Int): Offset = Offset(x * scale, y * scale)
val Offset.max get() = max(x, y)
val Offset.min get() = min(x, y)
operator fun Size.div(scale: Scale): Size = Size(width / scale.x, height / scale.y)
operator fun Size.div(scale: Int): Size = Size(width / scale, height / scale)
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
