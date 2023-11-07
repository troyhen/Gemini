package gemini.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toOffset

@JvmInline
value class IntRectangle private constructor(private val data: IntArray) {
    var left: Int
        get() = data[0]
        set(value) {
            data[0] = value
        }
    var top: Int
        get() = data[1]
        set(value) {
            data[1] = value
        }
    var right: Int
        get() = data[2]
        set(value) {
            data[2] = value
        }
    var bottom: Int
        get() = data[3]
        set(value) {
            data[3] = value
        }

    val isEmpty get() = left >= right || top >= bottom
    val isNotEmpty get() = !isEmpty

    constructor(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) : this(intArrayOf(left, top, right, bottom))

    fun overlaps(other: IntRectangle): Boolean {
        if (right <= other.left || other.right <= left) return false
        if (bottom <= other.top || other.bottom <= top) return false
        return true
    }

    fun set(left: Int, top: Int, right: Int, bottom: Int) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }

    fun set(offset: IntOffset, size: IntSize) {
        this.left = offset.x
        this.top = offset.y
        this.right = offset.x + size.width
        this.bottom = offset.y + size.height
    }

    fun set(offset: Offset, size: Size) {
        val intOffset = offset.round()
        val intSize = size.round()
        this.left = intOffset.x
        this.top = intOffset.y
        this.right = intOffset.x + intSize.width
        this.bottom = intOffset.y + intSize.height
    }

    fun setFrom(rectangle: IntRectangle) {
        repeat(4) { index ->
            data[index] = rectangle.data[index]
        }
    }

    companion object {
        val Zero = IntRectangle()
    }
}

val IntRectangle.center get() = IntOffset((right + left) / 2, (bottom + top) / 2)
val IntRectangle.size get() = IntSize(right - left, bottom - top)
val IntRectangle.topLeft get() = IntOffset(left, top)
