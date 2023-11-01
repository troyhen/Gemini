package gemini.foundation

import androidx.compose.ui.graphics.drawscope.DrawScope

class Tile<K: Any>(private val atlas: Atlas<K>, private val key: K): Draw {
    override fun DrawScope.draw() {
        atlas.run { draw(key) }
    }
}