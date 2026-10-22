package gemini.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix

enum class Pivot(val offset: Offset) {
    NorthWest(Offset.Zero), North(Offset(.5f, 0f)), NorthEast(Offset(1f, 0f)),
    West(Offset(0f, .5f)), Center(Offset(.5f, .5f)), East(Offset(1f, .5f)),
    SouthWest(Offset(0f, 1f)), South(Offset(.5f, 1f)), SouthEast(Offset(1f, 1f)),
}

data class Position(
    val location: Location = Location(),
    val space: Space = Space(),
    val pivot: Pivot = Pivot.Center,
    val rotation: Rotation = Rotation(),
    val scale: Scale = Scale(),
) {
    private val matrix: Matrix = Matrix()

    fun rectangle(rectangle: Rectangle) {
        val width = space.width * scale.x
        val height = space.height * scale.y
        val left = location.x - pivot.offset.x * width
        val top = location.y - pivot.offset.y * height
        rectangle.set(left, top, left + width, top + height)
    }

    fun transform(): Matrix = matrix.apply {
        reset()
        if (!location.isZero) translate(location.x, location.y, location.z)
        if (rotation.p.degrees != 0f) rotateX(rotation.p.degrees)
        if (rotation.y.degrees != 0f) rotateY(rotation.y.degrees)
        if (rotation.r.degrees != 0f) rotateZ(rotation.r.degrees)
        if (!scale.isOne) scale(scale.x, scale.y, scale.z)
        if (pivot != Pivot.NorthWest) translate(-pivot.offset.x * space.width, -pivot.offset.y * space.height)
    }
}
