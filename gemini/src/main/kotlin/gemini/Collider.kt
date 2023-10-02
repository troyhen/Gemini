package gemini

interface Collider {
    fun collideWith(collider: Collider): Boolean
}
