package gemini.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.time.Duration

@JvmInline
value class Velocity private constructor(private val data: FloatArray) {
    var xs: Float
        get() = data[0]
        set(value) {
            data[0] = value
        }
    var ys: Float
        get() = data[1]
        set(value) {
            data[1] = value
        }
    var zs: Float
        get() = data[2]
        set(value) {
            data[2] = value
        }

    constructor(xs: Float = 0f, ys: Float = 0f, zs: Float = 0f) : this(floatArrayOf(xs, ys, zs))
    constructor(speed: Offset, zs: Float = 0f) : this(floatArrayOf(speed.x, speed.y, zs))

    fun add(xs: Float, ys: Float, zs: Float = 0f) {
        this.xs += xs
        this.ys += ys
        this.zs += zs
    }

    fun add(delta: Offset, zs: Float = 0f) {
        this.xs += delta.x
        this.ys += delta.y
        this.zs += zs
    }

    fun set(xs: Float, ys: Float, zz: Float = 0f) {
        this.xs = xs
        this.ys = ys
        this.zs = zz
    }

    fun set(speed: Offset, zz: Float = 0f) {
        this.xs = speed.x
        this.ys = speed.y
        this.zs = zz
    }

    fun setFrom(velocity: Velocity) {
        repeat(3) { index ->
            data[index] = velocity.data[index]
        }
    }
}

operator fun Velocity.div(scale: Float) = Velocity(xs / scale, ys / scale, zs / scale)
operator fun Velocity.div(scale: Int) = Velocity(xs / scale, ys / scale, zs / scale)
operator fun Velocity.minus(velocity: Velocity) = Velocity(xs - velocity.xs, ys - velocity.ys, zs - velocity.zs)
operator fun Velocity.minus(offset: Offset) = Velocity(xs - offset.x, ys - offset.y, zs)
operator fun Velocity.plus(velocity: Velocity) = Velocity(xs + velocity.xs, ys + velocity.ys, zs + velocity.zs)
operator fun Velocity.plus(offset: Offset) = Velocity(xs + offset.x, ys + offset.y, zs)
operator fun Velocity.times(scale: Float) = Velocity(xs * scale, ys * scale, zs * scale)
operator fun Velocity.times(scale: Int) = Velocity(xs * scale, ys * scale, zs * scale)
operator fun Velocity.divAssign(scale: Float) {
    xs /= scale
    ys /= scale
    zs /= scale
}

operator fun Velocity.divAssign(scale: Int) = divAssign(scale.toFloat())
operator fun Velocity.minusAssign(velocity: Velocity) {
    xs -= velocity.xs
    ys -= velocity.ys
    zs -= velocity.zs
}

operator fun Velocity.minusAssign(offset: Offset) {
    xs -= offset.x
    ys -= offset.y
}

operator fun Velocity.plusAssign(velocity: Velocity) {
    xs += velocity.xs
    ys += velocity.ys
    zs += velocity.zs
}

operator fun Velocity.plusAssign(offset: Offset) {
    xs += offset.x
    ys += offset.y
}

operator fun Velocity.timesAssign(scale: Float) {
    xs *= scale
    ys *= scale
    zs *= scale
}

operator fun Velocity.timesAssign(scale: Int) = timesAssign(scale.toFloat())

fun Velocity.change(offset: Offset, time: Duration) {
    val seconds = time.seconds
    xs += offset.x * seconds
    ys += offset.y * seconds
}

fun Velocity.change(size: Size, time: Duration) {
    val seconds = time.seconds
    xs += size.width * seconds
    ys += size.height * seconds
}

fun Velocity.change(velocity: Velocity, time: Duration) {
    val seconds = time.seconds
    xs += velocity.xs * seconds
    ys += velocity.ys * seconds
    zs += velocity.zs * seconds
}

fun Velocity.negate() {
    xs = -xs
    ys = -ys
    zs = -zs
}

operator fun Velocity.unaryMinus(): Velocity = Velocity(-xs, -ys, -zs)
