package gemini

import androidx.compose.ui.graphics.drawscope.DrawScope

open class Thing(val orientation: Orientation = Orientation()) {
    open fun DrawScope.draw() = Unit
}
