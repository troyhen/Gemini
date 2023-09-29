package gemini

open class SceneScope : Scene() {

    var camera = Camera()
    var scene: Scene? = null

    fun build(): Scene {
        scene?.let {
            replaceAll(it)
        }
        return this
    }
}
