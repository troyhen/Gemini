package gemini

import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.time.Duration

open class Thing(val position: Position = Position()) {
    open suspend fun act(elapsed: Duration) = Unit
    open fun DrawScope.draw() = Unit

    open fun DrawScope.orientAndDraw() {
        drawContext.canvas.save()
        drawContext.transform.transform(position.orient())
        draw()
        drawContext.canvas.restore()
    }
}
