package gemini

import androidx.compose.ui.geometry.Offset

@JvmInline
value class Velocity private constructor(private val data: FloatArray) {
    var x: Float
        get() = data[0]
        set(value) {
            data[0] = value
        }
    var y: Float
        get() = data[1]
        set(value) {
            data[1] = value
        }
    var z: Float
        get() = data[2]
        set(value) {
            data[2] = value
        }

    val offset: Offset get() = Offset(x, y)

    constructor() : this(FloatArray(3))
    constructor(x: Float, y: Float, z: Float = 0f) : this(floatArrayOf(x, y, z))

    fun move(x: Float, y: Float, z: Float = 0f) {
        this.x += x
        this.y += y
        this.z += z
    }

    fun set(x: Float, y: Float, z: Float = 0f) {
        this.x = x
        this.y = y
        this.z = z
    }

    companion object {
        val Zero = Velocity()
    }
}

operator fun Velocity.div(scale: Float) = Velocity(x / scale, y / scale, z / scale)
operator fun Velocity.minus(velocity: Velocity) = Velocity(x - velocity.x, y - velocity.y, z - velocity.z)
operator fun Velocity.plus(velocity: Velocity) = Velocity(x + velocity.x, y + velocity.y, z + velocity.z)
operator fun Velocity.times(scale: Float) = Velocity(x * scale, y * scale, z * scale)
