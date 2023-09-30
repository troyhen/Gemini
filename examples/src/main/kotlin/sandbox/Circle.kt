package sandbox

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.Position
import gemini.SceneScope
import gemini.Thing
import kotlin.time.Duration

class Circle(
    position: Position,
    var color: Color,
    val actor: (suspend Circle.(Duration) -> Unit)? = null,
) : Thing(position) {

    override suspend fun act(elapsed: Duration) {
        actor?.invoke(this, elapsed)
    }

    override fun DrawScope.draw() {
        drawOval(color, position.location.offset, position.size)
    }
}

fun SceneScope.circle(radius: Float, color: Color, act: (suspend Circle.(Duration) -> Unit)? = null) {
    val diameter = radius * 2
    val thing = Circle(Position(size = Size(diameter, diameter)), color, act)
    add(thing)
}
