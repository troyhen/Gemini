package sandbox.astroids

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.*
import kotlin.math.cos
import kotlin.math.max
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
        if (position.location.x < 0) position.location.x += spaceSize.width
        if (position.location.y < 0) position.location.y += spaceSize.height
        if (position.location.x >= spaceSize.width) position.location.x -= spaceSize.width
        if (position.location.y >= spaceSize.height) position.location.y -= spaceSize.height
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

    companion object {
        const val PI2 = PI * 2
    }
}

fun SceneScope.asteroid(spaceSize: Size, size: Int = 4, act: (suspend Asteroid.(Duration) -> Unit)? = null) {
    val diameter = max(spaceSize.width, spaceSize.width) / 20 * size
    val speed = diameter / 2
    val location = Location(Random.nextFloat() * spaceSize.width, Random.nextFloat() * spaceSize.height)
    val thing = Asteroid(Position(location, Size(diameter, diameter)), speed)
    add(thing)
    act?.let {
        add {
            act(thing, it)
        }
    }
}
