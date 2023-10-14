package examples.sandbox

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.SceneScope
import gemini.foundation.Thing
import gemini.geometry.*
import kotlin.time.Duration

class Circle(
    position: Position,
    var color: Color,
    private val actor: (suspend Circle.(Duration) -> Unit)? = null,
) : Thing(position) {

    private val rectangle = Rectangle()

    override suspend fun act(elapsed: Duration) {
        actor?.invoke(this, elapsed)
    }

    override fun DrawScope.draw() {
        position.rectangle(rectangle)
        drawOval(color, rectangle.topLeft, rectangle.size)
    }
}

fun SceneScope.circle(radius: Float, color: Color, act: (suspend Circle.(Duration) -> Unit)? = null) {
    val diameter = radius * 2
    val thing = Circle(Position(space = Space(diameter, diameter)), color, act)
    add(thing)
}
