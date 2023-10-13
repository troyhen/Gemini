package examples.sandbox

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.SceneScope
import gemini.foundation.Thing
import gemini.geometry.Position
import gemini.geometry.Space
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
        drawOval(color, position.location.offset, position.space.size)
    }
}

fun SceneScope.circle(radius: Float, color: Color, act: (suspend Circle.(Duration) -> Unit)? = null) {
    val diameter = radius * 2
    val thing = Circle(Position(space = Space(diameter, diameter)), color, act)
    add(thing)
}
