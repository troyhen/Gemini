package gemini.foundation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.toSize
import gemini.engine.AfterCamera
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.geometry.Pivot
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.TimeSource

class FrameRate(private val color: Color, private val pivot: Pivot = Pivot.SouthEast, private val maxSamples: Int = 100) : Thing(), AfterCamera {
    private var lastMark: TimeSource.Monotonic.ValueTimeMark? = null
    private var frameRate = 0f
    private var samples = 0
    private var lastTenths = -1
    private var cachedLayout: TextLayoutResult? = null

    override suspend fun act(elapsed: Duration) {
        val end = Stage.time.markNow()
        lastMark?.let { start ->
            val micros = (end - start).inWholeMicroseconds
            if (micros > 0L) {
                if (samples < maxSamples) {
                    samples++
                }
                frameRate = (frameRate * (samples - 1) + 1e6f / micros) / samples // trigger recompose
            }
        }
        lastMark = end
    }

    private fun measureFrameRate(): TextLayoutResult? {
        val textMeasurer = Stage.instance?.textMeasurer ?: return null
        val tenths = (frameRate * 10f).roundToInt().coerceAtLeast(0)
        if (tenths == lastTenths && cachedLayout != null) {
            return cachedLayout
        }
        lastTenths = tenths
        val text = "${tenths / 10}.${tenths % 10} f/s"
        return textMeasurer.measure(text).also {
            cachedLayout = it
        }
    }

    override fun DrawScope.draw() {
        val stage = Stage.instance
        if (stage?.isRunning != true) return
        val layout = measureFrameRate() ?: return
        val textSize = layout.size.toSize()
        val center = Offset((size.width - textSize.width) / 2, (size.height - textSize.height) / 2)
        val offset = when (pivot) {
            Pivot.NorthEast -> Offset(size.width - textSize.width, 0f)
            Pivot.NorthWest -> Offset(0f, 0f)
            Pivot.North -> Offset(center.x, 0f)
            Pivot.West -> Offset(0f, center.y)
            Pivot.Center -> center
            Pivot.East -> Offset(size.width - textSize.width, center.y)
            Pivot.SouthWest -> Offset(0f, size.height - textSize.height)
            Pivot.South -> Offset(center.x, size.height - textSize.height)
            Pivot.SouthEast -> Offset(size.width - textSize.width, size.height - textSize.height)
        }
        drawRect(Color.Black, offset, textSize / density)
        drawText(layout, color, offset)
    }
}

fun SceneScope.frameRate(color: Color, pivot: Pivot = Pivot.SouthEast) {
    add(FrameRate(color, pivot))
}
