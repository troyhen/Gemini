package gemini.foundation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.toSize
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.engine.Stage.Companion.world
import gemini.geometry.Pivot
import gemini.max
import kotlin.time.Duration
import kotlin.time.TimeSource

class FrameRate(private val color: Color, private val pivot: Pivot = Pivot.SouthEast) : Thing() {
    private var lastMark: TimeSource.Monotonic.ValueTimeMark? = null
    private var frameRate by mutableStateOf(0f)

    override suspend fun act(elapsed: Duration) {
        val end = Stage.time.markNow()
        lastMark?.let { start ->
            frameRate = (frameRate * 59 + 1e6f / (end - start).inWholeMicroseconds) / 60 // trigger recompose
        }
        lastMark = end
    }

    private fun measureFrameRate(): TextLayoutResult? {
        val text = "%.1f f/s".format(frameRate)
        return Stage.instance?.textMeasurer?.measure(text)
    }

    override fun DrawScope.draw() {
        if (Stage.instance?.isRunning != true) return
        val layout = measureFrameRate() ?: return
        withTransform({
            scale(world.size.max / size.max, Offset.Zero)
            translate(-center.x, -center.y)
        }) {
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
            drawRect(Color.Black, offset, layout.size.toSize() / density)
            drawText(layout, color, offset)
        }
    }
}

fun SceneScope.frameRate(color: Color, pivot: Pivot = Pivot.SouthEast) {
    add(FrameRate(color, pivot))
}
