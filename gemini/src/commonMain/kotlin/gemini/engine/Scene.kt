package gemini.engine

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import gemini.asset.Asset
import gemini.asset.Image
import gemini.asset.Sound
import gemini.asset.Source
import gemini.foundation.Thing

open class Scene {
    val assets = mutableMapOf<Source, Asset>()
    private val _things = mutableListOf<Thing>()
    val things: List<Thing> = _things

    open fun add(thing: Thing) {
        _things.add(thing)
    }

    open fun image(source: Source): Image {
        return assets[source] as? Image ?: Image().also {
            assets[source] = it
        }
    }

    open fun remove(thing: Thing) {
        _things.remove(thing)
    }

    open fun replaceAll(scene: Scene) {
        assets.clear()
        scene.assets.forEach { (t, u) ->
            assets[t] = u
        }
        _things.clear()
        _things.addAll(scene.things)
    }

    open fun sound(source: Source): Sound {
        return assets[source] as? Sound ?: Sound().also {
            assets[source] = it
        }
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
