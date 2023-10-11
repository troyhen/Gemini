package examples.astroids

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import gemini.engine.Gemini
import gemini.engine.Stage
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.frameRate

@Composable
fun Game(state: State, modifier: Modifier = Modifier) = state.run {
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

    BoxWithConstraints {
        val screenSize = Size(maxWidth.value, maxHeight.value)
        val game = rememberScene(iteration) {
            background(Color.Black)
            frameRate(Color.White)
            ship(screenSize, ::onEnd) {
                control(state)
            }
            repeat(4) {
                asteroid(screenSize)
            }
        }
        Gemini(modifier.fillMaxSize()) {
            scene = game
        }
        if (showRestart) {
            TextButton(
                onClick = ::onRestart,
                modifier = Modifier.align(Alignment.Center),
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color.LightGray.copy(alpha = .5f)),
            ) {
                Text("Restart", style = MaterialTheme.typography.h5)
            }
        }
    }
}
