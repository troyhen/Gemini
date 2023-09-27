package gemini

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

class Background(private val color: Color) : Thing() {
    override fun DrawScope.orientAndDraw() {
        drawRect(color)
    }
}

fun SceneScope.background(color: Color) {
    add(Background(color))
}
