package gemini.engine

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import gemini.asset.Asset
import gemini.asset.Image
import gemini.asset.Sound
import gemini.asset.Source
import gemini.foundation.Atlas
import gemini.foundation.Thing
import gemini.foundation.imageMap
import gemini.foundation.tileMap
import gemini.geometry.IntRectangle

open class Scene {
    private val _assets = mutableMapOf<Source, Asset>()
    val assets: Map<Source, Asset> = _assets
    private val _things = mutableListOf<Thing>()
    val things: List<Thing> = _things

    open fun add(thing: Thing) {
        _things.add(thing)
    }

    open fun image(source: Source): Image {
        return assets[source] as? Image ?: Image().also {
            assets[source]?.release()
            _assets[source] = it
        }
    }

    open fun <K: Any> imageMap(source: Source, find: (key: K, rectangle: IntRectangle) -> Unit): Atlas<K> = imageMap(image(source), find)

    fun release() {
        assets.values.forEach {
            it.release()
        }
        _assets.clear()
        _things.clear()
    }

    open fun remove(thing: Thing) {
        _things.remove(thing)
    }

    open fun replaceAll(scene: Scene) {
        release()
        scene.assets.forEach { (t, u) ->
            _assets[t] = u
        }
        _things.addAll(scene.things)
    }

    open fun sound(source: Source): Sound {
        return assets[source] as? Sound ?: Sound().also {
            assets[source]?.release()
            _assets[source] = it
        }
    }

    open fun tileMap(source: Source, tileSize: IntSize, inset: IntOffset = IntOffset.Zero): Atlas<IntOffset> = tileMap(image(source), tileSize, inset)
}

@Composable
fun rememberScene(vararg keys: Any, builder: SceneScope.() -> Unit): Scene {
    return remember(keys) {
        SceneScope().run {
            builder()
            build()
        }
    }.also {
        DisposableEffect(keys) {
            onDispose {
                it.release()
            }
        }
    }
}
