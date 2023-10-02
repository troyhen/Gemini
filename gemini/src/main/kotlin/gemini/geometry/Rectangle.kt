package gemini.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

class Rectangle(var left: Float = 0f, var top: Float = 0f, var right: Float = 0f, var bottom: Float = 0f) {
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

    fun set(offset: Offset, size: Size) {
        this.left = offset.x
        this.top = offset.y
        this.right = offset.x + size.width
        this.bottom = offset.y + size.height
    }
}
