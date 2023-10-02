package gemini.engine

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import gemini.foundation.Thing

open class Scene {
    val things = mutableListOf<Thing>()

    @Synchronized
    open fun add(thing: Thing) {
        things.add(thing)
    }

    @Synchronized
    open fun remove(thing: Thing) {
        things.remove(thing)
    }

    @Synchronized
    open fun replaceAll(scene: Scene) {
        things.clear()
        things.addAll(scene.things)
    }
}

@Composable
fun rememberScene(vararg keys: Any, builder: SceneScope.() -> Unit): Scene {
    return remember(keys) {
        SceneScope().run {
            builder()
            build()
        }
    }
}
