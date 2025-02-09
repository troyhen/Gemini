package examples.astroids

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.Collider
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.foundation.MovingThing
import gemini.geometry.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class Ship(position: Position, private val onEnd: () -> Unit, private val onUpdate: Ship.(Duration) -> Unit) : MovingThing(position), Collider {

    private var lastFire: TimeSource.Monotonic.ValueTimeMark? = null

    private val path: Path by lazy {
        val size = position.space.size
        val center = size / 2f
        Path().apply {
            moveTo(size.width, center.height)
            lineTo(0f, size.height)
            lineTo(size.width / 4, center.height)
            lineTo(0f, 0f)
        }
    }

    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        wrap()
        val seconds = elapsed.seconds
        val friction = -seconds * .1f
        velocity.xs += velocity.xs * friction
        velocity.ys += velocity.ys * friction
        spin += spin * friction
        onUpdate(elapsed)
    }

    override fun collideWith(collider: Collider): Boolean {
        return when (collider) {
            is Asteroid -> {
                collider.explode()
                Stage.instance?.stop()
                onEnd()
                true
            }

            else -> false
        }
    }

    override fun DrawScope.drawRelative() {
        drawPath(path, Color.Cyan)
    }

    private fun fireBullet() {
        Stage.instance?.run {
            val end = position.space.size.width / 2
            val offset = Offset(1f, 0f) rotate position.rotation.r
            val tip = Location(offset * end)
            val speed = Velocity(offset * Bullet.SPEED)
            bullet(position.location + tip, velocity + speed)
        }
    }

    fun control(shipState: ShipState, elapsed: Duration) {
        val seconds = elapsed.seconds
        if (Control.GoForward in shipState.controls) {
            velocity.change(FORWARD_THRUST rotate position.rotation.r, elapsed)
        }
        if (Control.GoBackward in shipState.controls) {
            velocity.change(BACKWARD_THRUST rotate position.rotation.r, elapsed)
        }
        if (Control.TurnLeft in shipState.controls) {
            spin -= SPIN_INCREMENT * seconds
        }
        if (Control.TurnRight in shipState.controls) {
            spin += SPIN_INCREMENT * seconds
        }
        if (Control.Fire in shipState.controls) {
            val timeSinceLastFire = lastFire?.elapsedNow() ?: COOL_DOWN_TIME
            if (timeSinceLastFire >= COOL_DOWN_TIME) {
                fireBullet()
                lastFire = Stage.time.markNow()
            }
        }
    }

    companion object {
        private val COOL_DOWN_TIME = 1.seconds
        private val FORWARD_THRUST = Offset(.2f, 0f)
        private val BACKWARD_THRUST = FORWARD_THRUST / -2f
        private const val SPIN_INCREMENT = 60f
    }
}

fun SceneScope.ship(onEnd: () -> Unit, onUpdate: Ship.(Duration) -> Unit): Ship {
    val length = .05f
    val width = length / 2
    return Ship(Position(space = Space(length, width)), onEnd, onUpdate).also { thing ->
        add(thing)
    }
}