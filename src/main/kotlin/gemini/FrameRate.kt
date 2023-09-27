package gemini

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.toSize
import kotlin.time.TimeSource

class FrameRate(private val color: Color) : Thing() {
    private var lastMark: TimeSource.Monotonic.ValueTimeMark? = null
    private var frameRate by mutableStateOf(0f)

    override fun DrawScope.draw() {
        val layout = measureFrameRate() ?: return
        val offset = Offset(size.width - layout.size.width / density, layout.size.height / density)
        drawRect(Color.Black, offset, layout.size.toSize() / density)
        drawText(layout, color, offset)
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
}

fun SceneScope.frameRate(color: Color) {
    add(FrameRate(color))
}
