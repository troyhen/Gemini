package gemini.engine

open class SceneScope : Scene() {
    val camera = Camera()
    var scene: Scene? = null

    fun build(): Scene {
        scene?.let {
            replaceAll(it)
        }
        return this
    }
}
