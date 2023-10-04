package gemini

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
operator fun Size.div(scale: Scale): Size = Size(width / scale.x, height / scale.y)
operator fun Size.div(scale: Int): Size = Size(width / scale, height / scale)
val Size.max get() = max(width, height)
val Size.min get() = min(width, height)
operator fun Size.minus(plus: Size): Size = Size(width - plus.width, height - plus.height)
operator fun Size.plus(plus: Size): Size = Size(width + plus.width, height + plus.height)
operator fun Size.times(scale: Scale): Size = Size(width * scale.x, height * scale.y)
operator fun Size.times(scale: Int): Size = Size(width * scale, height * scale)
fun Size.toOffset(): Offset = Offset(width, height)
