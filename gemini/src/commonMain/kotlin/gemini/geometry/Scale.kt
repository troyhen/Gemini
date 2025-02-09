package gemini.geometry

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

    constructor(x: Float = ONE.x, y: Float = ONE.y, z: Float = ONE.z) : this(floatArrayOf(x, y, z))

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

    fun setFrom(scale: Scale) {
        data[0] = scale.data[0]
        data[1] = scale.data[1]
        data[2] = scale.data[2]
    }

    companion object {
        val ONE = Scale(1f, 1f, 1f)
    }
}
