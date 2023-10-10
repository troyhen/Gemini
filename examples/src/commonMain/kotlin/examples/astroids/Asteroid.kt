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
import gemini.foundation.particles
import gemini.geometry.*
import gemini.random
import gemini.randomPlus
import gemini.rotate
import gemini.toOffset
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


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
                val distance = s2.randomPlus(s2)
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
            asteroids(3, position.location, position.size, velocity, spin, baseSize)
            particles(20, position.location, velocity, 40f, 2.seconds, Color.White)
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
    val spin = 30.degrees.randomPlus()
    return Asteroid(Position(location, Size(diameter, diameter)), velocity, spin).also {
        add(it)
    }
}

private fun SceneScope.asteroids(number: Int, location: Location, size: Size, velocity: Velocity, spin: Angle, baseSize: Int) {
    repeat(number) {
        add(
            Asteroid(
                position = Position(location + Offset(size.width, size.height).randomPlus(), size / 2f),
                velocity = velocity * 1.5f + Offset(20f, 20f).randomPlus(),
                spin = spin * 2 + 10.degrees.randomPlus(),
                baseSize = baseSize / 2,
            )
        )
    }
}
