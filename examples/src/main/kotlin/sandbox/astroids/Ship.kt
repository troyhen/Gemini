package sandbox.astroids

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class Ship(position: Position, private val onUpdate: Ship.() -> Unit) : MovingThing(position), Collider {

    private var lastFire: TimeSource.Monotonic.ValueTimeMark? = null

    private val path: Path by lazy {
        Path().apply {
            moveTo(position.size.width / 2, 0f)
            lineTo(-position.size.width / 2, position.size.height / 2)
            lineTo(-position.size.width / 4, 0f)
            lineTo(-position.size.width / 2, -position.size.height / 2)
        }
    }

    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        super.act(elapsed)
        wrap()
        val seconds = elapsed.inSeconds
        val friction = -seconds * .1f
        velocity.x += velocity.x * friction
        velocity.y += velocity.y * friction
        spin += spin * friction
        onUpdate()
    }

    override fun collidesWith(collider: Collider): Boolean {
        return when (collider) {
            is MovingThing -> !Rect(position.location.offset, position.size).intersect(Rect(collider.position.location.offset, collider.position.size)).isEmpty
            else -> false
        }
    }

    override fun DrawScope.draw() {
        drawPath(path, Color.Cyan)
    }

    private fun fireBullet() {
        Stage.instance?.run {
            val end = position.size.width / 2
            val dx = cos(position.rotation.r.radians)
            val dy = sin(position.rotation.r.radians)
            val tip = Location(end * dx, end * dy, 0f)
            val speed = Velocity(30 * dx, 30 * dy, 0f)
            bullet(position.location + tip, velocity + speed)
        }
    }

    fun update(forward: Boolean, left: Boolean, right: Boolean, backward: Boolean, fire: Boolean) {
        if (forward) {
            velocity.x += .2f * cos(position.rotation.r.radians)
            velocity.y += .2f * sin(position.rotation.r.radians)
        }
        if (backward) {
            velocity.x -= .1f * cos(position.rotation.r.radians)
            velocity.y -= .1f * sin(position.rotation.r.radians)
        }
        if (left) {
            spin -= .2f
        }
        if (right) {
            spin += .2f
        }
        if (fire) {
            val timeSinceLastFire = lastFire?.elapsedNow() ?: COOL_DOWN_TIME
            if (timeSinceLastFire >= COOL_DOWN_TIME) {
                fireBullet()
            }
            lastFire = Stage.time.markNow()
        }
    }

    companion object {
        private val COOL_DOWN_TIME = 2.seconds
    }
}

fun SceneScope.ship(spaceSize: Size, onUpdate: Ship.() -> Unit): Ship {
    val location = Location(spaceSize.width / 2, spaceSize.height / 2)
    val length = min(spaceSize.width, spaceSize.height) / 20
    val width = length / 2
    return Ship(Position(location, Size(length, width)), onUpdate).also { thing ->
        add(thing)
    }
}