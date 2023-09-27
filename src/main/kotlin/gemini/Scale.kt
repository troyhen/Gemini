package gemini

import androidx.compose.ui.geometry.Size

@JvmInline
value class Scale private constructor(private val data: FloatArray) {
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

    val size: Size get() = Size(x, y)

    constructor() : this(FloatArray(3))
    constructor(x: Float, y: Float, z: Float = ONE.z) : this(floatArrayOf(x, y, z))

    fun scale(x: Float, y: Float, z: Float = ONE.z) {
        this.x *= x
        this.y *= y
        this.z *= z
    }

    fun set(x: Float, y: Float, z: Float = ONE.z) {
        this.x = x
        this.y = y
        this.z = z
    }

    companion object {
        val ONE = Scale(1f, 1f, 1f)
    }
}
