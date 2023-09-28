package gemini

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import io.kamel.core.Resource
import io.kamel.core.getOrNull
import kotlin.time.Duration

class Sprite(
    location: Location,
    size: Size = Size.Zero,
    pivot: Pivot = Pivot.Center,
    val painter: Painter? = null,
    val resource: Resource<Painter>? = null,
) : Thing(Position(location, size, pivot = pivot)) {

    override fun DrawScope.draw() {
        (painter ?: resource?.getOrNull())?.run {
            draw(position.size.takeUnless { it == Size.Zero } ?: intrinsicSize)
        }
    }
}

fun SceneScope.sprite(painter: Painter, location: Location, size: Size = Size.Zero, pivot: Pivot = Pivot.Center, act: (suspend Sprite.(Duration) -> Unit)? = null) {
    val thing = Sprite(location, size, pivot, painter)
    add(thing)
    act?.let {
        add { elapsed ->
            act(thing, elapsed)
        }
    }
}

fun SceneScope.sprite(resource: Resource<Painter>, location: Location, size: Size = Size.Zero, pivot: Pivot = Pivot.Center, act: (suspend Sprite.(Duration) -> Unit)? = null) {
    val thing = Sprite(location, size, pivot, resource = resource)
    add(thing)
    act?.let {
        add { elapsed ->
            act(thing, elapsed)
        }
    }
}
