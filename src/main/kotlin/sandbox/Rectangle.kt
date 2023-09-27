package sandbox

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.*
import kotlin.time.Duration

class Rectangle(
    x: Float, y: Float,
    width: Float, height: Float,
    private val color: Color,
    private val action: (Rectangle.(Duration) -> Unit)? = null,
) : Thing(Orientation(location = Location(x, y), scale = Scale(width, height))), Actor {

    override suspend fun act(elapsed: Duration) {
        action?.invoke(this, elapsed)
    }

    override fun DrawScope.draw() {
        drawRect(color, orientation.location.offset, orientation.scale.size)
    }
}

fun GeminiScope.rectangle(x: Float, y: Float, width: Float, height: Float, color: Color, action: (Rectangle.(Duration) -> Unit)? = null) {
    add(Rectangle(x, y, width, height, color, action))
}
