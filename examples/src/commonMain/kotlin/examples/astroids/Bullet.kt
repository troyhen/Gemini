package examples.astroids

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.Collider
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.foundation.MovingThing
import gemini.geometry.Location
import gemini.geometry.Position
import gemini.geometry.Space
import gemini.geometry.Velocity
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class Bullet(position: Position, speed: Velocity) : MovingThing(position, speed), Collider {

    private val startTime: TimeSource.Monotonic.ValueTimeMark = Stage.time.markNow()

    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        wrap()
        if (Stage.time.markNow() - startTime > LIFE_SPAN) Stage.instance?.remove(this)
    }

    override fun collideWith(collider: Collider): Boolean {
        return when (collider) {
            is Asteroid -> {
                Stage.instance?.remove(this)
                collider.explode()
                true
            }

            else -> false
        }
    }

    override fun DrawScope.draw() {
        drawRect(Color.Red, position.location.offset, position.space.size)
    }

    companion object {
        private val LIFE_SPAN = 4.seconds
        const val SIZE = .004f
        const val SPEED = .35f
    }
}

fun SceneScope.bullet(location: Location, speed: Velocity, size: Float = Bullet.SIZE): Bullet {
    return Bullet(Position(location, Space(size, size)), speed).also { thing ->
        add(thing)
    }
}
