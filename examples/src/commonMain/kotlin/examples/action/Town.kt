package examples.action

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import gemini.asset.Source
import gemini.engine.Gemini
import gemini.foundation.Atlas
import gemini.foundation.background
import gemini.foundation.cycle
import gemini.foundation.sprite
import kotlin.time.Duration.Companion.seconds

@Composable
fun Town(tileMap: Source, modifier: Modifier = Modifier) {
    Gemini(modifier) {
        background(Color.Black)
        val atlas = tileMap(tileMap, IntSize(64, 64))
        val tiles = 0..7
        val north = cycle(4.seconds, atlas, tiles.map { IntOffset(it, 0) })
        val west = cycle(4.seconds, atlas, tiles.map { IntOffset(it, 1) })
        val south = cycle(4.seconds, atlas, tiles.map { IntOffset(it, 2) })
        val east = cycle(4.seconds, atlas, tiles.map { IntOffset(it, 3) })
        sprite(south, Offset(100f, 100f), Size(100f, 100f))
    }
}
