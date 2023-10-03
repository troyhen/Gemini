package gemini

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import gemini.geometry.Angle
import gemini.geometry.Scale
import gemini.geometry.degrees
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration

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
val Float.random: Float get() = Random.nextFloat() * this
val Int.random: Int get() = Random.nextInt(this)
val Long.random: Long get() = Random.nextLong(this)
val Offset.random: Offset get() = Offset(x.random, y.random)
val Size.random: Size get() = Size(width.random, height.random)
fun Offset.rotate(angle: Angle): Offset {
    val c = cos(angle.radians)
    val s = sin(angle.radians)
    return Offset(x * c - y * s, y * c + x * s)
}

operator fun Size.times(scale: Scale): Size = Size(width * scale.x, height * scale.y)
fun Size.toOffset(): Offset = Offset(width, height)
