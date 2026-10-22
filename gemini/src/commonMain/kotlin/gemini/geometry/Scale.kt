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

    val isOne: Boolean get() = x == 1f && y == 1f && z == 1f

    constructor(x: Float = 1f, y: Float = 1f, z: Float = 1f) : this(floatArrayOf(x, y, z))

    fun contentEquals(other: Scale): Boolean = x == other.x && y == other.y && z == other.z

    fun scale(x: Float, y: Float, z: Float = 1f) {
        this.x *= x
        this.y *= y
        this.z *= z
    }

    fun set(x: Float, y: Float, z: Float = 1f) {
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
        val ONE get() = Scale(1f, 1f, 1f)
    }
}
