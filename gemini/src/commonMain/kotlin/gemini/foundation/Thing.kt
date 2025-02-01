package gemini.foundation

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import gemini.geometry.Position
import kotlin.time.Duration

open class Thing(val position: Position = Position()) : Draw {
    open suspend fun act(elapsed: Duration) = Unit

    override fun DrawScope.draw() {
        withTransform({ transform(position.transform()) }) {
            drawRelative()
        }
    }

    open fun DrawScope.drawRelative() = Unit
}
