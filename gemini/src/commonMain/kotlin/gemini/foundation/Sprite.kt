package gemini.foundation

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import gemini.asset.Image
import gemini.engine.SceneScope
import gemini.geometry.*
import io.kamel.core.Resource
import io.kamel.core.getOrNull
import kotlin.time.Duration

class Sprite(
    position: Position,
    val onDraw: DrawScope.(Sprite) -> Unit,
    val actor: (suspend Sprite.(Duration) -> Unit)? = null,
) : Thing(position) {

    val rectangle = Rectangle()

    override suspend fun act(elapsed: Duration) {
        actor?.invoke(this, elapsed)
    }

    override fun DrawScope.draw() {
        drawRelative {
            onDraw(this@Sprite)
        }
    }
}

fun SceneScope.sprite(
    image: Image,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    pivot: Pivot = Pivot.Center,
    act: (suspend Sprite.(Duration) -> Unit)? = null
): Sprite {
    return Sprite(Position(Location(x, y), Space(width, height), pivot = pivot), {
        image.run {
            it.position.rectangle(it.rectangle)
            draw(it.rectangle)
        }
    }, actor = act).also {
        add(it)
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
    return Sprite(Position(Location(x, y), Space(width, height), pivot = pivot), {
        painter.run {
            draw(Size(width, height))
        }
    }, actor = act).also {
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
    return Sprite(Position(Location(x, y), Space(width, height), pivot = pivot), {
        resource.getOrNull()?.run {
            draw(Size(width, height))
        }
    }, actor = act).also {
        add(it)
    }
}
