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
    x: Float, y: Float,
    width: Float, height: Float,
    private val color: Color,
) : Thing(Position(location = Location(x, y), size = Size(width, height))) {

    override fun DrawScope.draw() {
        drawRect(color, Offset.Zero, position.size)
    }
}

fun SceneScope.rectangle(x: Float, y: Float, width: Float, height: Float, color: Color, act: (suspend MovingRectangle.(Duration) -> Unit)? = null) {
    val thing = MovingRectangle(x, y, width, height, color)
    add(thing)
    act?.let {
        add {
            act(thing, it)
        }
    }
}
