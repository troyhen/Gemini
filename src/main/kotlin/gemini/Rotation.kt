package gemini

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

    constructor() : this(FloatArray(3))
    constructor(r: Float, p: Float = ZERO.degrees, y: Float = ZERO.degrees) : this(floatArrayOf(r, p, y))

    fun rotate(r: Angle, y: Angle = ZERO, p: Angle = ZERO) {
        this.r += r
        this.p += p
        this.y += y
    }

    fun set(r: Angle, p: Angle = ZERO, y: Angle = ZERO) {
        this.r = r
        this.p = p
        this.y = y
    }

    companion object {
        val ZERO = Angle()
    }
}
