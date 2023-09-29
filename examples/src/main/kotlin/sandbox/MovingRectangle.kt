package sandbox

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.Location
import gemini.Position
import gemini.SceneScope
import gemini.Thing
import kotlin.time.Duration

class MovingRectangle(
    position: Position,
    private val color: Color,
) : Thing(position) {

    override fun DrawScope.draw() {
        drawRect(color, Offset.Zero, position.size)
    }
}

fun SceneScope.rectangle(x: Float, y: Float, width: Float, height: Float, color: Color, act: (suspend MovingRectangle.(Duration) -> Unit)? = null) {
    val thing = MovingRectangle(Position(location = Location(x, y), size = Size(width, height)), color)
    add(thing)
    act?.let {
        add {
            act(thing, it)
        }
    }
}
