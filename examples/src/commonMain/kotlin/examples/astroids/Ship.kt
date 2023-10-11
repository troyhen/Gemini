package examples.astroids

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.Collider
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.foundation.MovingThing
import gemini.geometry.*
import gemini.inSeconds
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class Ship(position: Position, private val onEnd: () -> Unit, private val onUpdate: Ship.() -> Unit) : MovingThing(position), Collider {

    private var lastFire: TimeSource.Monotonic.ValueTimeMark? = null

    private val path: Path by lazy {
        val size = position.size
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
        val seconds = elapsed.inSeconds
        val friction = -seconds * .1f
        velocity.xs += velocity.xs * friction
        velocity.ys += velocity.ys * friction
        spin += spin * friction
        onUpdate()
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

    override fun DrawScope.draw() {
        drawPath(path, Color.Cyan)
    }

    private fun fireBullet() {
        Stage.instance?.run {
            val end = position.size.width / 2
            val dx = cos(position.rotation.r.radians)
            val dy = sin(position.rotation.r.radians)
            val tip = Location(end * dx, end * dy, 0f)
            val speed = Velocity(BULLET_SPEED * dx, BULLET_SPEED * dy)
            bullet(position.location + tip, velocity + speed)
        }
    }

    fun control(state: State) {
        if (Control.GoForward in state.controls) {
            velocity.xs += FORWARD_THRUST * cos(position.rotation.r.radians)
            velocity.ys += FORWARD_THRUST * sin(position.rotation.r.radians)
        }
        if (Control.GoBackward in state.controls) {
            velocity.xs -= BACKWARD_THRUST * cos(position.rotation.r.radians)
            velocity.ys -= BACKWARD_THRUST * sin(position.rotation.r.radians)
        }
        if (Control.TurnLeft in state.controls) {
            spin -= SPIN_INCREMENT
        }
        if (Control.TurnRight in state.controls) {
            spin += SPIN_INCREMENT
        }
        if (Control.Fire in state.controls) {
            val timeSinceLastFire = lastFire?.elapsedNow() ?: COOL_DOWN_TIME
            if (timeSinceLastFire >= COOL_DOWN_TIME) {
                fireBullet()
                lastFire = Stage.time.markNow()
            }
        }
    }

    companion object {
        private val COOL_DOWN_TIME = 1.seconds
        private const val FORWARD_THRUST = .4f
        private const val BACKWARD_THRUST = FORWARD_THRUST / 2
        private const val BULLET_SPEED = 100f
        private const val SPIN_INCREMENT = .5f
    }
}

fun SceneScope.ship(spaceSize: Size, onEnd: () -> Unit, onUpdate: Ship.() -> Unit): Ship {
    val location = Location(spaceSize.width / 2, spaceSize.height / 2)
    val length = min(spaceSize.width, spaceSize.height) / 20
    val width = length / 2
    return Ship(Position(location, Size(length, width)), onEnd, onUpdate).also { thing ->
        add(thing)
    }
}