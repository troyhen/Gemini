package examples.sandbox

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.SceneScope
import gemini.foundation.Thing
import gemini.geometry.Location
import gemini.geometry.Position
import kotlin.time.Duration

class MovingRectangle(
    position: Position,
    private val color: Color,
    val actor: (suspend MovingRectangle.(Duration) -> Unit)? = null,
) : Thing(position) {

    override suspend fun act(elapsed: Duration) {
        actor?.invoke(this, elapsed)
    }

    override fun DrawScope.draw() {
        drawRect(color, Offset.Zero, position.size)
    }
}

fun SceneScope.rectangle(x: Float, y: Float, width: Float, height: Float, color: Color, act: (suspend MovingRectangle.(Duration) -> Unit)? = null): MovingRectangle {
    return MovingRectangle(Position(location = Location(x, y), size = Size(width, height)), color, act).also {
        add(it)
    }
}
