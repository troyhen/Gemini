package gemini.foundation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.geometry.*
import gemini.randomPlus
import kotlin.time.Duration
import kotlin.time.TimeSource

class Particle(position: Position, speed: Velocity, private val lifeSpan: Duration, private val color: Color) : MovingThing(position, speed) {

    private val startTime: TimeSource.Monotonic.ValueTimeMark = Stage.time.markNow()

    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        wrap()
        if (Stage.time.markNow() - startTime > lifeSpan) Stage.instance?.remove(this)
    }

    override fun DrawScope.draw() {
        drawRect(color, position.location.offset, position.space.size)
    }

    companion object {
        const val SIZE = .002f
    }
}

fun SceneScope.particles(number: Int, location: Location, speed: Velocity, randomness: Float = 0f, lifeSpan: Duration, color: Color): List<Particle> {
    return (1..number).map {
        val offset = Offset(randomness, randomness).randomPlus()
        Particle(Position(location + offset, Space(Particle.SIZE, Particle.SIZE)), speed + offset, lifeSpan.randomPlus(), color).also { thing ->
            add(thing)
        }
    }
}
