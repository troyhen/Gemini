package gemini.foundation

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import gemini.engine.Stage
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

class Cycle(cycleTime: Duration, private val frames: Int, private val onDraw: DrawScope.(Int) -> Unit) : Draw {

    private var _currentFrame = 0
    var currentFrame: Int get() = _currentFrame
        set(value) {
            start?.let {
                start = Stage.time.markNow() - frameTime * value
            }
            _currentFrame = value
        }

    private val frameTime = cycleTime / frames
    private var start: TimeSource.Monotonic.ValueTimeMark? = null

    init {
        require(frames > 0) { "Must have at least 1 frame: $frames" }
        require(cycleTime.isFinite()) { "cycleTime is not finite: $cycleTime" }
        require(cycleTime > 1.milliseconds) { "cycleTime is too short: $cycleTime" }
        start()
    }

    override fun DrawScope.draw() {
        start?.let {
            _currentFrame = ((Stage.time.markNow() - it) / frameTime).toInt() % frames
        }
        onDraw(_currentFrame)
    }

    fun start() {
        start = Stage.time.markNow() - frameTime * _currentFrame
    }

    fun stop() {
        start = null
    }
}

fun cycle(cycleTime: Duration, frames: List<Draw>): Cycle {
    return Cycle(cycleTime, frames.size) { frame ->
        frames[frame].run { draw() }
    }
}

fun cycle(cycleTime: Duration, atlas: Atlas<IntOffset>, frames: List<IntOffset>): Cycle {
    return Cycle(cycleTime, frames.size) { frame ->
        atlas.run { draw(frames[frame]) }
    }
}
