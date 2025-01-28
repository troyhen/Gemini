package examples.beads

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.Collider
import gemini.engine.SceneScope
import gemini.foundation.MovingThing
import gemini.geometry.*

class Bead(position: Position, private val color: Color) : MovingThing(position), Collider {
    private val radiusSquared = position.space.width * position.space.width / 4f
    private val mass = position.space.width * position.space.width * position.space.width
    private val tempVelocity1 = Velocity()
    private val tempVelocity2 = Velocity()
    private val tempVelocity3 = Velocity()

    override fun collideWith(collider: Collider): Boolean {
        when (collider) {
            is Bead -> {
                val collided = distanceSquared(position.location, collider.position.location) <= radiusSquared + collider.radiusSquared
                if (collided) {
                    val mp = mass + collider.mass
                    tempVelocity1.setFrom(velocity)
                    tempVelocity2.setFrom(collider.velocity)
                    tempVelocity1 *= BOUNCE * (mass - collider.mass) / mp
                    tempVelocity2 *= BOUNCE * (2f * collider.mass) / mp
                    tempVelocity1 += tempVelocity2
                    tempVelocity2.setFrom(velocity)
                    tempVelocity3.setFrom(collider.velocity)
                    tempVelocity2 *= BOUNCE * (2f * mass) / mp
                    tempVelocity3 *= BOUNCE * (collider.mass - mass) / mp
                    tempVelocity2 += tempVelocity3
                    velocity.setFrom(tempVelocity1)
                    collider.velocity.setFrom(tempVelocity2)
                }
            }
        }
        return true
    }

    override fun DrawScope.draw() {
        drawCircle(color, position.space.width / 2)
    }

    companion object {
        const val BOUNCE = .9f
    }
}

fun SceneScope.bead(color: Color, location: Location, diameter: Float = 1f): Bead {
    return Bead(Position(location = location, space = Space(diameter, diameter, diameter)), color).also { thing ->
        add(thing)
    }
}