package gemini.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

@JvmInline
value class World private constructor(private val data: FloatArray) {
    constructor(left: Float = -1f, right: Float = 1f, top: Float = -1f, bottom: Float = 1f, near: Float = -1f, far: Float = 1f) : this(floatArrayOf(left, right, top, bottom, near, far))

    var left: Float
        get() = data[0]
        set(value) {
            data[0] = value
        }
    var right: Float
        get() = data[1]
        set(value) {
            data[1] = value
        }
    var top: Float
        get() = data[2]
        set(value) {
            data[2] = value
        }
    var bottom: Float
        get() = data[3]
        set(value) {
            data[3] = value
        }
    var near: Float
        get() = data[4]
        set(value) {
            data[4] = value
        }
    var far: Float
        get() = data[5]
        set(value) {
            data[5] = value
        }

    val depth: Float get() = far - near
    val height: Float get() = bottom - top
    val offset: Offset get() = Offset(left, top)
    val size: Size get() = Size(width, height)
    val width: Float get() = right - left

    fun set(left: Float = -1f, right: Float = 1f, top: Float = -1f, bottom: Float = 1f, near: Float = -1f, far: Float = 1f) {
        this.left = left
        this.right = right
        this.top = top
        this.bottom = bottom
        this.near = near
        this.far = far
    }

    fun set(world: World) {
        left = world.left
        right = world.right
        top = world.top
        bottom = world.bottom
        near = world.near
        far = world.far
    }
}
