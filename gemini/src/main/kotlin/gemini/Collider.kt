package gemini

fun interface Collider {
    fun collidesWith(collider: Collider): Boolean
}
