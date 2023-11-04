package examples.astroids

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import gemini.engine.Gemini
import gemini.engine.Stage
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.frameRate
import gemini.geometry.Pivot

@Composable
fun Game(shipState: ShipState, modifier: Modifier = Modifier) = shipState.run {
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
            camera.orthographic()
            background(Color.Black)
            frameRate(Color.White, Pivot.NorthEast)
            ship(::onEnd) {
                control(shipState)
            }
            repeat(4) {
                asteroid()
            }
        }
        Gemini(Modifier.fillMaxSize()) {
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
