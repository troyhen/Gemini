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

    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        wrap()
        if (Stage.time.markNow() - startTime > LIFE_SPAN) Stage.instance?.remove(this)
    }

    override fun DrawScope.draw() {
        drawOval(Color.Red, position.location.offset, position.size)
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
    }
}
