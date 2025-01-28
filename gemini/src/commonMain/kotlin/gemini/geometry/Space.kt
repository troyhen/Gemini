package gemini.geometry

import androidx.compose.ui.geometry.Size

@JvmInline
value class Space private constructor(private val data: FloatArray) {
    constructor(width: Float = 1f, height: Float = 1f, depth: Float = 1f) : this(floatArrayOf(width, height, depth))
    constructor(size: Size, depth: Float = 1f) : this(floatArrayOf(size.width, size.height, depth))

    var width: Float
        get() = data[0]
        set(value) {
            data[0] = value
        }
    var height: Float
        get() = data[1]
        set(value) {
            data[1] = value
        }
    var depth: Float
        get() = data[2]
        set(value) {
            data[2] = value
        }

    fun scale(xs: Float = 1f, ys: Float = 1f, zs: Float = 1f) {
        width *= xs
        height *= ys
        depth *= zs
    }

    fun set(width: Float = 1f, height: Float = 1f, depth: Float = 1f) {
        this.width = width
        this.height = height
        this.depth = depth
    }

    fun setFrom(space: Space) {
        data[0] = space.data[0]
        data[1] = space.data[1]
        data[2] = space.data[2]
    }

    val size: Size get() = Size(width, height)
}
