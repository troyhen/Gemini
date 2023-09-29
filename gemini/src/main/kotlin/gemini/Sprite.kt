package gemini

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import io.kamel.core.Resource
import io.kamel.core.getOrNull
import kotlin.time.Duration

class Sprite(
    position: Position,
    val painter: Painter? = null,
    val resource: Resource<Painter>? = null,
) : Thing(position) {

    override fun DrawScope.draw() {
        (painter ?: resource?.getOrNull())?.run {
            draw(position.size.takeUnless { it == Size.Zero } ?: intrinsicSize)
        }
    }
}

fun SceneScope.sprite(painter: Painter, x: Float, y: Float, width: Float, height: Float, pivot: Pivot = Pivot.Center, act: (suspend Sprite.(Duration) -> Unit)? = null) {
    val thing = Sprite(Position(Location(x, y), Size(width, height), pivot = pivot), painter = painter)
    add(thing)
    act?.let {
        add { elapsed ->
            act(thing, elapsed)
        }
    }
}

fun SceneScope.sprite(resource: Resource<Painter>, x: Float, y: Float, width: Float, height: Float, pivot: Pivot = Pivot.Center, act: (suspend Sprite.(Duration) -> Unit)? = null) {
    val thing = Sprite(Position(Location(x, y), Size(width, height), pivot = pivot), resource = resource)
    add(thing)
    act?.let {
        add { elapsed ->
            act(thing, elapsed)
        }
    }
}
