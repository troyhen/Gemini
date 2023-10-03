package examples.astroids

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.Collider
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.foundation.MovingThing
import gemini.geometry.*
import gemini.random
import gemini.rotate
import gemini.toOffset
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.time.Duration


class Asteroid(
    position: Position,
    velocity: Velocity,
    spin: Angle,
    private val baseSize: Int = 4,
) : MovingThing(position, velocity, spin), Collider {

    private val path: Path by lazy {
        val center = (position.size / 2f).toOffset()
        val s2 = center.x / 2
        Path().apply {
            val step = 360f / 12
            repeat(12) {
                val distance = s2.random + s2
                val angle = (it * step).degrees
                val offset = center + Offset(distance, 0f).rotate(angle)
                if (it == 0) {
                    moveTo(offset.x, offset.y)
                } else {
                    lineTo(offset.x, offset.y)
                }
            }
        }
    }

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

    override fun DrawScope.draw() {
        drawPath(path, Color.Gray)
    }

    fun explode() {
        Stage.instance?.run {
            remove(this@Asteroid)
            if (baseSize <= 1) return
            add(
                Asteroid(
                    position = Position(position.location + Offset(position.size.width, position.size.height).random, position.size / 2f),
                    velocity = velocity * 1.5f + Offset(20f, 20f).random,
                    spin = spin * 2 + 10.degrees.random - 5.degrees,
                    baseSize = baseSize / 2,
                )
            )
            add(
                Asteroid(
                    position = Position(position.location + Offset(position.size.width, position.size.height).random, position.size / 2f),
                    velocity = velocity * 1.5f + Offset(20f, 20f).random,
                    spin = spin * 2 + 10.degrees.random - 5.degrees,
                    baseSize = baseSize / 2,
                )
            )
            add(
                Asteroid(
                    position = Position(position.location + Offset(position.size.width, position.size.height).random, position.size / 2f),
                    velocity = velocity * 1.5f + Offset(20f, 20f).random,
                    spin = spin * 2 + 10.degrees.random - 5.degrees,
                    baseSize = baseSize / 2,
                )
            )
        }
    }
}

fun SceneScope.asteroid(screenSize: Size, size: Int = 4): Asteroid {
    val ratio = min(screenSize.width, screenSize.height) / 30
    val diameter = ratio * size
    val speed = ratio
    val location = Location(screenSize.width.random, screenSize.height.random)
    val direction = PI2.random
    val velocity = Velocity(speed * cos(direction), speed * sin(direction))
    val spin = 30.degrees.random - 15.degrees
    return Asteroid(Position(location, Size(diameter, diameter)), velocity, spin).also {
        add(it)
    }
}
