package gemini.foundation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.SceneScope
import gemini.engine.Stage

class Background(private val color: Color) : Thing() {
    override fun DrawScope.draw() {
        val world = Stage.visible
        drawRect(color, world.offset, world.size)
    }
}

fun SceneScope.background(color: Color) {
    add(Background(color))
}
