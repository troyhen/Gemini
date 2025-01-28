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
    override fun collideWith(collider: Collider): Boolean {
        return when (collider) {
            is Bowl -> {
//                val delta = (collider.position.location - position.location).normalize()
//                position.location.x -= delta.x
//                position.location.y -= delta.y
//                collider.position.location.x += delta.x
//                collider.position.location.y += delta.y
                true
            }

            else -> true
        }
    }

    override fun DrawScope.draw() = drawRelative {
        drawCircle(color, position.space.width / 2)
    }
}

fun SceneScope.bowl(location: Location, radius: Float, color: Color): Bowl {
    val diameter = radius * 2
    return Bowl(Position(location, Space(diameter, diameter)), color).also { thing ->
        add(thing)
    }
}