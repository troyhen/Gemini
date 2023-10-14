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

    override fun replaceAll(scene: Scene) {
        super.replaceAll(scene)
        if (scene is SceneScope) {
            camera.setFrom(scene.camera)
        }
    }
}
