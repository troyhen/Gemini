package gemini.engine

import gemini.geometry.World

open class SceneScope : Scene() {
    var camera = Camera()
    var scene: Scene? = null
    var visible: World = World()
    var world: World = World()

    fun build(): Scene {
        scene?.let {
            replaceAll(it)
        }
        return this
    }
}
