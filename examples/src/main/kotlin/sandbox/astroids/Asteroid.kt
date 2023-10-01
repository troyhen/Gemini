package sandbox.astroids

import androidx.compose.ui.geometry.Offset
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

class Asteroid(
    position: Position,
    speed: Float,
) : MovingThing(position), Collider {

    init {
        val direction = Random.nextFloat() * PI2
        velocity.set(speed * cos(direction), speed * sin(direction))
    }

    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        wrap()
    }

    override fun DrawScope.draw() {
        drawOval(Color.Gray, Offset.Zero, position.size)
    }

    override fun collidesWith(collider: Collider): Boolean {
        return when (collider) {
            is MovingThing -> !Rect(position.location.offset, position.size).intersect(Rect(collider.position.location.offset, collider.position.size)).isEmpty
            else -> false
        }
    }
}

fun SceneScope.asteroid(screenSize: Size, size: Int = 4): Asteroid {
    val ratio = min(screenSize.width, screenSize.height) / 30
    val diameter = ratio * size
    val speed = ratio
    val location = Location(Random.nextFloat() * screenSize.width, Random.nextFloat() * screenSize.height)
    return Asteroid(Position(location, Size(diameter, diameter)), speed).also {
        add(it)
    }
}
