package gemini.engine

interface Collider {
    fun collideWith(collider: Collider): Boolean
}
