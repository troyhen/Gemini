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
            camera.orthographic(-16f, 16f, -16f, 16f, -16f, 16f)
            background(Color.Black)
            frameRate(Color.White, Pivot.NorthEast)
            val main = bowl(Location(), 10f, Color.DarkGray)
            repeat(BOWLS) {
                val angle = Angle(it * 360f / BOWLS)
                bowl(Location(8f * angle.cos(), 8f * angle.sin()), 4f, Color.Red)
            }
            repeat(BEADS) {
                val x = main.position.space.width.randomPlus()
                val y = main.position.space.width.randomPlus()
                bead(Color.Blue, main.position.location + Offset(x, y))
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

private const val BOWLS = 6
private const val BEADS = 20