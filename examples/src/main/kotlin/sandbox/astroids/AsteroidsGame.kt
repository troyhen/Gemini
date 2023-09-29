package sandbox.astroids

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.window.singleWindowApplication
import gemini.Gemini
import gemini.background
import gemini.rememberScene

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AsteroidsGame(modifier: Modifier = Modifier) {
    var iteration by remember { mutableStateOf(1) }
    fun restart() {
        iteration++
    }

    var gameState by remember { mutableStateOf(true) }
    var mouse by remember { mutableStateOf(Offset(0f, 0f)) }
    var pressed by remember { mutableStateOf(false) }
    BoxWithConstraints(modifier.fillMaxSize()) {
        val spaceSize = Size(maxWidth.value, maxHeight.value)
        val game = rememberScene(iteration) {
            background(Color.Black)
            ship(Size(maxWidth.value, maxHeight.value)) { time ->
                act(spaceSize, time, false, false)
            }
            repeat(4) {
                asteroid(spaceSize) { time ->
                    act(time)
                }
            }
        }
        Gemini(Modifier.fillMaxSize().onPointerEvent(PointerEventType.Move) {
            mouse = it.changes.last().position
        }.onPointerEvent(PointerEventType.Press) {
            pressed = true
        }.onPointerEvent(PointerEventType.Release) {
            pressed = false
        }) {
            scene = game
        }
    }
}

fun main() = singleWindowApplication(title = "Gemini Asteroids") {
    AsteroidsGame()
}
