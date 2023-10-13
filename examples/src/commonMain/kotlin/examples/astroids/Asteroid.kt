package examples.astroids

import androidx.compose.ui.geometry.Offset
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
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Asteroid(
    position: Position,
    velocity: Velocity,
    spin: Angle,
    val baseSize: Int = 4,
) : MovingThing(position, velocity, spin), Collider {

    private val path: Path by lazy {
        val center = (position.space.size / 2f).toOffset()
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

    override fun DrawScope.draw() = drawRelative {
        drawPath(path, Color.Gray)
    }

    fun explode() {
        Stage.instance?.run {
            remove(this@Asteroid)
            particles(20 * baseSize, position.location, velocity, .1f, 2.seconds, Color.White)
            if (baseSize <= 1) return
            asteroids(3, this@Asteroid)
        }
    }
}

fun SceneScope.asteroid(size: Int = 4): Asteroid {
    val ratio = 1f / 30
    val diameter = ratio * size
    val speed = ratio
    val location = Location(2f.randomPlus(), 2f.randomPlus())
    val direction = PI2.random
    val velocity = Velocity(speed * cos(direction), speed * sin(direction))
    val spin = 30.degrees.randomPlus()
    return Asteroid(Position(location, Space(diameter, diameter)), velocity, spin).also {
        add(it)
    }
}

private fun SceneScope.asteroids(number: Int, parent: Asteroid) {
    val size = parent.position.space.size
    repeat(number) {
        Asteroid(
            position = Position(parent.position.location + Offset(size.width, size.height).randomPlus(), Space(size / 2f)),
            velocity = parent.velocity * 1.5f + Offset(.05f, .05f).randomPlus(),
            spin = parent.spin * 2 + 10.degrees.randomPlus(),
            baseSize = parent.baseSize / 2,
        ).also {
            add(it)
        }
    }
}
