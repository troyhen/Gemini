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
    val actor: (suspend Sprite.(Duration) -> Unit)? = null,
) : Thing(position) {

    override suspend fun act(elapsed: Duration) {
        actor?.invoke(this, elapsed)
    }

    override fun DrawScope.draw() {
        (painter ?: resource?.getOrNull())?.run {
            draw(position.size.takeUnless { it == Size.Zero } ?: intrinsicSize)
        }
    }
}

fun SceneScope.sprite(
    painter: Painter,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    pivot: Pivot = Pivot.Center,
    act: (suspend Sprite.(Duration) -> Unit)? = null
): Sprite {
    return Sprite(Position(Location(x, y), Size(width, height), pivot = pivot), painter = painter, actor = act).also {
        add(it)
    }
}

fun SceneScope.sprite(
    resource: Resource<Painter>,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    pivot: Pivot = Pivot.Center,
    act: (suspend Sprite.(Duration) -> Unit)? = null
): Sprite {
    return Sprite(Position(Location(x, y), Size(width, height), pivot = pivot), resource = resource, actor = act).also {
        add(it)
    }
}
