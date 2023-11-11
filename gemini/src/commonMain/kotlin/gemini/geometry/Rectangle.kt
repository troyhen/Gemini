package gemini.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toOffset

@JvmInline
value class Rectangle private constructor(private val data: FloatArray) {
    var left: Float
        get() = data[0]
        set(value) {
            data[0] = value
        }
    var top: Float
        get() = data[1]
        set(value) {
            data[1] = value
        }
    var right: Float
        get() = data[2]
        set(value) {
            data[2] = value
        }
    var bottom: Float
        get() = data[3]
        set(value) {
            data[3] = value
        }

    val isEmpty get() = left >= right || top >= bottom

    constructor(left: Float = 0f, top: Float = 0f, right: Float = 0f, bottom: Float = 0f) : this(floatArrayOf(left, top, right, bottom))

    fun contains(offset: Offset): Boolean = offset.x >= left && offset.x < right && offset.y >= top && offset.y < bottom

    fun overlaps(other: Rectangle): Boolean {
        if (right <= other.left || other.right <= left) return false
        if (bottom <= other.top || other.bottom <= top) return false
        return true
    }

    fun set(left: Float, top: Float, right: Float, bottom: Float) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }

    fun set(offset: IntOffset, size: IntSize) {
        val float = offset.toOffset()
        this.left = float.x
        this.top = float.y
        this.right = float.x + size.width
        this.bottom = float.y + size.height
    }

    fun set(offset: Offset, size: Size) {
        this.left = offset.x
        this.top = offset.y
        this.right = offset.x + size.width
        this.bottom = offset.y + size.height
    }

    fun setFrom(rectangle: Rectangle) {
        repeat(4) { index ->
            data[index] = rectangle.data[index]
        }
    }

    companion object {
        val Zero = Rectangle()
    }
}

val Rectangle.center get() = Offset((right + left) * .5f, (bottom + top) * .5f)
val Rectangle.size get() = Size(right - left, bottom - top)
val Rectangle.topLeft get() = Offset(left, top)
