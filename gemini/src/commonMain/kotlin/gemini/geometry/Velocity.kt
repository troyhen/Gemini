package gemini.geometry

import androidx.compose.ui.geometry.Offset

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
