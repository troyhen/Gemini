package examples.beads

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import gemini.engine.Gemini
import gemini.engine.HandlePointer
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.frameRate
import gemini.geometry.*
import kotlinx.coroutines.flow.StateFlow

@Composable
fun BeadGame(pointerFlow: StateFlow<Offset?>, modifier: Modifier = Modifier) {
    var dragging by remember { mutableStateOf<Bead?>(null) }
    val beads = remember { mutableListOf<Bead>() }

    Box(modifier) {
        val game = rememberScene {
            beads.clear()
            camera.default()
            background(Color.Black)
            frameRate(Color.White, Pivot.NorthEast)
            val main = bowl(Location(450f, 450f), 250f, Color.DarkGray)
            val step = 360.degrees / BOWLS
            repeat(BOWLS) {
                val angle = step * it
                bowl(main.position.location + Offset(350f * angle.cos(), 350f * angle.sin()), 100f, Color.Gray)
            }
            colors.forEach { color ->
                repeat(BEADS) {
                    beads.add(bead(main.position.location + Offset(main.radius, main.radius).randomPlus(), color))
                }
            }
        }
        Gemini {
            scene = game
        }
    }

    HandlePointer(
        pointerFlow = pointerFlow,
        drop = { dragging = null },
        drag = { offset ->
            if (dragging == null) {
                dragging = beads.find { it.isHit(offset) }
            }
            dragging?.let { bead ->
                bead.position.location.set(offset.x, offset.y)
                bead.velocity.set(0f, 0f)
            }
        },
    )
}

private const val BOWLS = 6
private const val BEADS = 15
private val colors = listOf(Color.Red, Color.Blue, Color.Yellow, Color.Cyan, Color.Green, Color.Magenta)