package gemini

import androidx.compose.ui.geometry.Offset

@JvmInline
value class Location private constructor(private val data: FloatArray) {
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
    constructor(offset: Offset) : this(floatArrayOf(offset.x, offset.y, 0f))
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
        val Zero = Location()
    }
}

operator fun Location.div(scale: Float) = Location(x / scale, y / scale, z / scale)
operator fun Location.div(scale: Int) = div(scale.toFloat())
operator fun Location.minus(location: Location) = Location(x - location.x, y - location.y, z - location.z)
operator fun Location.minus(offset: Offset) = Location(x - offset.x, y - offset.y, z)
operator fun Location.plus(location: Location) = Location(x + location.x, y + location.y, z + location.z)
operator fun Location.plus(offset: Offset) = Location(x + offset.x, y + offset.y, z)
operator fun Location.times(scale: Float) = Location(x * scale, y * scale, z * scale)
operator fun Location.times(scale: Int) = times(scale.toFloat())
