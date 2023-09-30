package sandbox.astroids

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class Bullet(position: Position, speed: Velocity) : MovingThing(position, speed), Collider {

    private val startTime: TimeSource.Monotonic.ValueTimeMark = Stage.time.markNow()

    fun act(spaceSize: Size, elapsed: Duration) {
        if (Stage.time.markNow() - startTime > LIFE_SPAN) Stage.instance?.remove(this)
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

    companion object {
        private val LIFE_SPAN = 5.seconds
    }
}

fun SceneScope.bullet(location: Location, speed: Velocity, size: Size = Size(3f, 3f)): Bullet {
    return Bullet(Position(location, size), speed).also { thing ->
        add(thing)
        add {
            thing.act(Stage.screenSize, it)
        }
    }
}