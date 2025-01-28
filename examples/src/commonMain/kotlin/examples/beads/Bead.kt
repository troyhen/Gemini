package examples.beads

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.Collider
import gemini.engine.SceneScope
import gemini.foundation.MovingThing
import gemini.geometry.*
import kotlin.time.Duration

class Bead(position: Position, private val color: Color) : MovingThing(position), Collider {

    private val radius = position.space.width / 2
    private val mass = position.space.width * position.space.width * position.space.width
    private val tempVelocity1 = Velocity()
    private val tempVelocity2 = Velocity()
    private val tempVelocity3 = Velocity()

    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        tempVelocity1.setFrom(velocity)
        tempVelocity1.negate()
        velocity.change(tempVelocity1, elapsed)
    }

    override fun collideWith(collider: Collider): Boolean {
        when (collider) {
            is Bead -> {
                val r2 = radius + collider.radius
                val collided = distanceSquared(position.location, collider.position.location) <= r2 * r2
                if (collided) {
                    val force = radius + collider.radius - distance(collider.position.location, position.location)
                    val delta = (collider.position.location - position.location).normalize() * (force / 2f)
                    position.location -= delta
                    collider.position.location += delta

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

            is Bowl -> {
                val r2 = radius + collider.radius
                val collided = distanceSquared(position.location, collider.position.location) <= r2 * r2
                if (collided) {
                    val force = -distance(collider.position.location, position.location)
                    val delta = (collider.position.location - position.location).normalize() * force
                    velocity.xs -= delta.x
                    velocity.ys -= delta.y
                }
            }
        }
        return true
    }

    override fun DrawScope.draw() = drawRelative {
        drawCircle(color, radius)
    }

    companion object {
        const val BOUNCE = .3f
    }
}

fun SceneScope.bead(location: Location, color: Color, diameter: Float = 40f): Bead {
    return Bead(Position(location = location, space = Space(diameter, diameter)), color).also { thing ->
        add(thing)
    }
}