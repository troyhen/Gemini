package gemini.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import gemini.times

enum class Pivot(val offset: Offset) {
    NorthWest(Offset.Zero), North(Offset(.5f, 0f)), NorthEast(Offset(1f, 0f)),
    West(Offset(0f, .5f)), Center(Offset(.5f, .5f)), East(Offset(1f, .5f)),
    SouthWest(Offset(0f, 1f)), South(Offset(.5f, 1f)), SouthEast(Offset(1f, 1f)),
}

data class Position(
    val location: Location = Location(),
    val size: Size = Size(1f, 1f),
    val rotation: Rotation = Rotation(),
    val scale: Scale = Scale(),
    val pivot: Pivot = Pivot.Center,
) {
    private val matrix: Matrix = Matrix()

    fun rectangle(rectangle: Rectangle) {
        val size = size * scale
        when (pivot) {
            Pivot.NorthWest -> rectangle.set(location.offset, size)
            Pivot.North -> rectangle.set(location.offset - Offset(size.width / 2, 0f), size)
            Pivot.NorthEast -> rectangle.set(location.offset - Offset(size.width, 0f), size)
            Pivot.West -> rectangle.set(location.offset - Offset(0f, size.height / 2), size)
            Pivot.Center -> rectangle.set(location.offset - Offset(size.width / 2, size.height / 2), size)
            Pivot.East -> rectangle.set(location.offset - Offset(size.width, size.height / 2), size)
            Pivot.SouthWest -> rectangle.set(location.offset - Offset(0f, size.height), size)
            Pivot.South -> rectangle.set(location.offset - Offset(size.width / 2, size.height), size)
            Pivot.SouthEast -> rectangle.set(location.offset - Offset(size.width, size.height), size)
        }
    }

    fun orient(): Matrix = matrix.apply {
        reset()
        if (location != Location.Zero) translate(location.x, location.y, location.z)
        if (rotation.p.degrees != 0f) rotateX(rotation.p.degrees)
        if (rotation.y.degrees != 0f) rotateY(rotation.y.degrees)
        if (rotation.r.degrees != 0f) rotateZ(rotation.r.degrees)
        if (scale != Scale.ONE) scale(scale.x, scale.y, scale.z)
        if (pivot != Pivot.NorthWest) translate(-pivot.offset.x * size.width, -pivot.offset.y * size.height)
    }
}
