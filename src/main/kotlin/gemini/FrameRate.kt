package gemini

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.toSize

class FrameRate(private val color: Color) : Thing() {

    override fun DrawScope.draw() {
        val stage = Stage.instance ?: return
        val layout = stage.measureFrameRate()
        val offset = Offset(size.width - layout.size.width / density, layout.size.height / density)
        drawRect(Color.Black, offset, layout.size.toSize() / density)
        drawText(layout, color, offset)
    }
}

fun SceneScope.frameRate(color: Color) {
    add(FrameRate(color))
}
