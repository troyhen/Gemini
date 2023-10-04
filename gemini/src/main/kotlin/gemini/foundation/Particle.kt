package gemini.foundation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.geometry.Location
import gemini.geometry.Position
import gemini.geometry.Velocity
import gemini.geometry.plus
import gemini.random
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
//        drawPoints(listOf(Offset.Zero), PointMode.Points, color)
        drawRect(color, Offset.Zero, Size(1f, 1f))
    }
}

fun SceneScope.particles(number: Int, location: Location, speed: Velocity, randomness: Float = 0f, lifeSpan: Duration, color: Color): List<Particle> {
    val r2 = randomness / 2
    val center = Offset(-r2, -r2)
    return (1..number).map {
        val offset = Offset(randomness, randomness).random + center
        Particle(Position(location + offset), speed + offset, lifeSpan.random, color).also { thing ->
            add(thing)
        }
    }
}
