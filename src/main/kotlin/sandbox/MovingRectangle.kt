package sandbox

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.*
import kotlin.time.Duration

class MovingRectangle(
    x: Float, y: Float,
    width: Float, height: Float,
    private val color: Color,
) : Thing(Orientation(location = Location(x, y), scale = Scale(width, height))) {

    override fun DrawScope.draw() {
        drawRect(color, orientation.location.offset, orientation.scale.size)
    }
}

fun SceneScope.rectangle(x: Float, y: Float, width: Float, height: Float, color: Color, act: (suspend (MovingRectangle, Duration) -> Unit)? = null) {
    val thing = MovingRectangle(x, y, width, height, color)
    add(thing)
    act?.let {
        add {
            act(thing, it)
        }
    }
}
