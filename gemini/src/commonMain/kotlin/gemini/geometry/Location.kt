package gemini.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.sqrt
import kotlin.time.Duration

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

    constructor(offset: Offset, z: Float = 0f) : this(offset.x, offset.y, z)
    constructor(x: Float = 0f, y: Float = 0f, z: Float = 0f) : this(floatArrayOf(x, y, z))

    fun move(xs: Float, ys: Float, zs: Float = 0f) {
        this.x += xs
        this.y += ys
        this.z += zs
    }

    fun set(x: Float, y: Float, z: Float = 0f) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun setFrom(location: Location) {
        data[0] = location.data[0]
        data[1] = location.data[1]
        data[2] = location.data[2]
    }

    companion object {
        val Zero = Location()
    }
}

operator fun Location.div(scale: Float): Location = Location(x / scale, y / scale, z / scale)
operator fun Location.div(scale: Int): Location = div(scale.toFloat())
operator fun Location.minus(location: Location): Location = Location(x - location.x, y - location.y, z - location.z)
operator fun Location.minus(offset: Offset): Location = Location(x - offset.x, y - offset.y, z)
operator fun Location.plus(location: Location): Location = Location(x + location.x, y + location.y, z + location.z)
operator fun Location.plus(offset: Offset): Location = Location(x + offset.x, y + offset.y, z)
operator fun Location.times(scale: Float): Location = Location(x * scale, y * scale, z * scale)
operator fun Location.times(scale: Int): Location = times(scale.toFloat())

operator fun Location.divAssign(scale: Float) {
    x /= scale
    y /= scale
    z /= scale
}

operator fun Location.divAssign(scale: Int) = divAssign(scale.toFloat())
operator fun Location.minusAssign(location: Location) {
    x -= location.x
    y -= location.y
    z -= location.z
}

operator fun Location.minusAssign(offset: Offset) {
    x -= offset.x
    y -= offset.y
}

operator fun Location.plusAssign(location: Location) {
    x += location.x
    y += location.y
    z += location.z
}

operator fun Location.plusAssign(offset: Offset) {
    x += offset.x
    y += offset.y
}

operator fun Location.timesAssign(scale: Float) {
    x *= scale
    y *= scale
    z *= scale
}

operator fun Location.timesAssign(scale: Int) = timesAssign(scale.toFloat())

fun Location.change(offset: Offset, time: Duration) {
    val seconds = time.seconds
    x += offset.x * seconds
    y += offset.y * seconds
}

fun Location.change(size: Size, time: Duration) {
    val seconds = time.seconds
    x += size.width * seconds
    y += size.height * seconds
}

fun Location.change(velocity: Velocity, time: Duration) {
    val seconds = time.seconds
    x += velocity.xs * seconds
    y += velocity.ys * seconds
    z += velocity.zs * seconds
}

fun distance(location1: Location, location2: Location): Float = sqrt(distanceSquared(location1, location2))
fun distanceSquared(location1: Location, location2: Location): Float {
    val dx = location1.x - location2.x
    val dy = location1.y - location2.y
    val dz = location1.z - location2.z
    return dx * dx + dy * dy + dz * dz
}

fun Location.normalize(): Location {
    if (x != 0f || y != 0f || z != 0f) {
        this /= sqrt(x * x + y * y + z * z)
    }
    return this
}

fun Location.normalized(): Location = Location(x, y, z).normalize()
