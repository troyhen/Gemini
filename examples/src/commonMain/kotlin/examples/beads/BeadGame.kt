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
            camera.default()
            background(Color.Black)
            frameRate(Color.White, Pivot.NorthEast)
            val main = bowl(Location(100f, 100f), 200f, Color.DarkGray)
            val step = 360.degrees / BOWLS
            repeat(BOWLS) {
                val angle = step * it
                bowl(Location(300f * angle.cos(), 300f * angle.sin()), 100f, Color.Gray)
            }
            colors.forEach { color ->
                repeat(BEADS) {
                    val x = main.position.space.width.randomPlus()
                    val y = main.position.space.width.randomPlus()
                    bead(color, main.position.location + Offset(x, y))
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

private const val BOWLS = 6
private const val BEADS = 15
private val colors = listOf(Color.Red, Color.Blue, Color.Yellow, Color.Cyan, Color.Green, Color.Magenta)