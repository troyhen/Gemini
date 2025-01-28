package examples.beads

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import gemini.engine.Gemini
import gemini.engine.Stage
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.frameRate
import gemini.geometry.*
import kotlinx.coroutines.flow.StateFlow

@Composable
fun BeadGame(modifier: Modifier = Modifier) {
    var iteration by remember { mutableStateOf(0) }
    var showRestart by remember { mutableStateOf(false) }

    fun onEnd() {
        showRestart = true
    }

    fun onRestart() {
        showRestart = false
        iteration++
        Stage.instance?.start()
    }

    Box(modifier) {
        val game = rememberScene(iteration) {
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
                    bead(main.position.location + Offset(main.radius, main.radius).randomPlus(), color)
                }
            }
        }
        Gemini {
            scene = game
        }
        if (showRestart) {
            TextButton(
                onClick = ::onRestart,
                modifier = Modifier.align(Alignment.Center),
                colors = ButtonDefaults.textButtonColors(containerColor = Color.DarkGray.copy(alpha = .5f)),
            ) {
                Text("Restart", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}


@Composable
private fun HandlePointer(pointerFlow: StateFlow<Offset?>, drop: (Offset) -> Unit, drag: (Offset?) -> Unit) {
    var lastPointer by remember { mutableStateOf<Offset?>(null) }
    val pointer = pointerFlow.collectAsState().value
    LaunchedEffect(pointer) {
        if (pointer == null) {
            lastPointer?.let { drop(it) }
        } else {
            drag(pointer)
        }
        lastPointer = pointer
    }
}

private const val BOWLS = 6
private const val BEADS = 15
private val colors = listOf(Color.Red, Color.Blue, Color.Yellow, Color.Cyan, Color.Green, Color.Magenta)