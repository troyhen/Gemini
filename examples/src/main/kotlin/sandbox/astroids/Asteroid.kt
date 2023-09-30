package sandbox.astroids

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration

class Asteroid(position: Position, speed: Float) : MovingThing(position), Collider {

    init {
        val direction = Random.nextFloat() * PI2
        velocity.set(speed * cos(direction), speed * sin(direction))
    }

    fun act(spaceSize: Size, elapsed: Duration) {
        act(elapsed)
        wrap(spaceSize)
    }

    override fun DrawScope.draw() {
        drawOval(Color.Gray, position.location.offset, position.size)
    }

    override fun collidesWith(collider: Collider): Boolean {
        return when (collider) {
            is MovingThing -> !Rect(position.location.offset, position.size).intersect(Rect(collider.position.location.offset, collider.position.size)).isEmpty
            else -> false
        }
    }
}

fun SceneScope.asteroid(spaceSize: Size, size: Int = 4, act: (suspend Asteroid.(Duration) -> Unit)? = null): Asteroid {
    val ratio = min(spaceSize.width, spaceSize.width) / 20
    val diameter = ratio * size
    val speed = ratio * .75f
    val location = Location(Random.nextFloat() * spaceSize.width, Random.nextFloat() * spaceSize.height)
    return Asteroid(Position(location, Size(diameter, diameter)), speed).also { thing ->
        add(thing)
        act?.let {
            add {
                act(thing, it)
            }
        }
    }
}
