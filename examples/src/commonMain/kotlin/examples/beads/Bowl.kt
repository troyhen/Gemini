package examples.beads

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.Collider
import gemini.engine.SceneScope
import gemini.foundation.Thing
import gemini.geometry.Location
import gemini.geometry.Position
import gemini.geometry.Space

class Bowl(position: Position, private val color: Color) : Thing(position), Collider {

    val radius = position.space.width / 2

    override fun collideWith(collider: Collider): Boolean {
        return when (collider) {
            is Bowl -> true
            else -> false
        }
    }

    override fun DrawScope.draw() = drawRelative {
        drawCircle(color, radius)
    }
}

fun SceneScope.bowl(location: Location, radius: Float, color: Color): Bowl {
    val diameter = radius * 2
    return Bowl(Position(location, Space(diameter, diameter)), color).also { thing ->
        add(thing)
    }
}