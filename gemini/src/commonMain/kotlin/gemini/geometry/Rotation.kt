package gemini.geometry

@JvmInline
value class Rotation private constructor(private val data: FloatArray) {
    var r: Angle
        get() = Angle(data[0])
        set(value) {
            data[0] = value.degrees
        }
    var p: Angle
        get() = Angle(data[1])
        set(value) {
            data[1] = value.degrees
        }
    var y: Angle
        get() = Angle(data[2])
        set(value) {
            data[2] = value.degrees
        }

    constructor(r: Float = ZERO.degrees, p: Float = ZERO.degrees, y: Float = ZERO.degrees) : this(floatArrayOf(r, p, y))

    fun rotate(r: Angle, p: Angle = ZERO, y: Angle = ZERO) {
        this.r += r
        this.p += p
        this.y += y
    }

    fun set(r: Angle, p: Angle = ZERO, y: Angle = ZERO) {
        this.r = r
        this.p = p
        this.y = y
    }

    fun setFrom(rotation: Rotation) {
        data[0] = rotation.data[0]
        data[1] = rotation.data[1]
        data[2] = rotation.data[2]
    }

    companion object {
        val ZERO = Angle()
    }
}
