package gemini.foundation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.WindowPosition.PlatformDefault.x
import androidx.compose.ui.window.WindowPosition.PlatformDefault.y
import gemini.asset.Image
import gemini.engine.SceneScope
import gemini.geometry.*
import io.kamel.core.Resource
import io.kamel.core.getOrNull
import kotlin.math.max
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
        position.rectangle(rectangle)
        drawRelative {
            onDraw(this@Sprite)
        }
    }
}

fun SceneScope.sprite(
    draw: Draw,
    offset: Offset,
    size: Size,
    pivot: Pivot = Pivot.Center,
    act: (suspend Sprite.(Duration) -> Unit)? = null
): Sprite {
    return Sprite(
        position = Position(Location(offset), Space(size), pivot = pivot),
        onDraw = { sprite ->
            scale(sprite.rectangle.size.max, Offset.Zero) {
                draw.run { draw() }
            }
        },
        actor = act
    ).also {
        add(it)
    }
}

fun SceneScope.sprite(
    painter: Painter,
    offset: Offset,
    size: Size,
    pivot: Pivot = Pivot.Center,
    act: (suspend Sprite.(Duration) -> Unit)? = null
): Sprite {
    return Sprite(
        position = Position(Location(offset), Space(size), pivot = pivot),
        onDraw = { sprite ->
            painter.run {
                scale(sprite.rectangle.size.max / intrinsicSize.max, Offset.Zero) {
                    draw(sprite.rectangle.size)
                }
            }
        },
        actor = act
    ).also {
        add(it)
    }
}

fun SceneScope.sprite(
    resource: Resource<Painter>,
    offset: Offset,
    size: Size,
    pivot: Pivot = Pivot.Center,
    act: (suspend Sprite.(Duration) -> Unit)? = null
): Sprite {
    return Sprite(
        position = Position(Location(offset), Space(size), pivot = pivot),
        onDraw = { sprite ->
            resource.getOrNull()?.run {
                scale(sprite.rectangle.size.max / intrinsicSize.max, Offset.Zero) {
                    draw(sprite.rectangle.size)
                }
            }
        },
        actor = act
    ).also {
        add(it)
    }
}
