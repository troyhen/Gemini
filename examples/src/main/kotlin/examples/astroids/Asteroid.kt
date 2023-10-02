package examples.astroids

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.Collider
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.foundation.MovingThing
import gemini.geometry.*
import gemini.random
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.time.Duration

class Asteroid(
    position: Position,
    velocity: Velocity,
    private val size: Int = 4,
) : MovingThing(position, velocity), Collider {

    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        wrap()
    }

    override fun collideWith(collider: Collider): Boolean {
//        return when (collider) {
//            is Asteroid -> {
//                explode()
//                collider.explode()
//                true
//            }

//            else -> false
//        }
        return false
    }

    fun explode() {
        Stage.instance?.run {
            remove(this@Asteroid)
            if (size <= 1) return
            add(
                Asteroid(
                    position = Position(position.location + Offset(position.size.width, position.size.height).random, position.size / 2f),
                    velocity = velocity * 1.5f + Offset(20f, 20f).random,
                    size = size / 2
                )
            )
            add(
                Asteroid(
                    position = Position(position.location + Offset(position.size.width, position.size.height).random, position.size / 2f),
                    velocity = velocity * 1.5f + Offset(20f, 20f).random,
                    size = size / 2
                )
            )
            add(
                Asteroid(
                    position = Position(position.location + Offset(position.size.width, position.size.height).random, position.size / 2f),
                    velocity = velocity * 1.5f + Offset(20f, 20f).random,
                    size = size / 2
                )
            )
        }
    }

    override fun DrawScope.draw() {
        drawOval(Color.Gray, Offset.Zero, position.size)
    }
}

fun SceneScope.asteroid(screenSize: Size, size: Int = 4): Asteroid {
    val ratio = min(screenSize.width, screenSize.height) / 30
    val diameter = ratio * size
    val speed = ratio
    val location = Location(screenSize.width.random, screenSize.height.random)
    val direction = PI2.random
    val velocity = Velocity(speed * cos(direction), speed * sin(direction))
    return Asteroid(Position(location, Size(diameter, diameter)), velocity).also {
        add(it)
    }
}
