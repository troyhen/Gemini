package gemini

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

open class Scene {
    val actors = mutableListOf<Actor>()
    val things = mutableListOf<Thing>()

    @Synchronized
    open fun add(actor: Actor) {
        actors.add(actor)
    }

    @Synchronized
    open fun add(thing: Thing) {
        things.add(thing)
        if (thing is Actor) {
            actors.add(thing)
        }
    }

    @Synchronized
    open fun replaceAll(scene: Scene) {
        actors.clear()
        things.clear()
        actors.addAll(scene.actors)
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
