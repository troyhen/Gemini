package gemini

data class Orientation(
    val location: Location = Location(),
    val rotation: Rotation = Rotation(),
    val scale: Scale = Scale()
) {
}
