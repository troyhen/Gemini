package examples.astroids

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.window.singleWindowApplication
import gemini.engine.Gemini
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.frameRate

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AsteroidsGame(modifier: Modifier = Modifier) {
    var iteration by remember { mutableStateOf(1) }
    fun restart() {
        iteration++
    }

    var gameState by remember { mutableStateOf(true) }
    val keys = remember { mutableSetOf<Key>() }
    val pressed = remember { mutableSetOf<Int>() }
    BoxWithConstraints(modifier.fillMaxSize()) {
        val screenSize = Size(maxWidth.value, maxHeight.value)
        val game = rememberScene(iteration) {
            background(Color.Black)
            frameRate(Color.White)
            ship(screenSize) {
                input(Key.DirectionUp in keys, Key.DirectionLeft in keys, Key.DirectionRight in keys, Key.DirectionDown in keys, Key.Spacebar in keys || 0 in pressed)
            }
            repeat(4) {
                asteroid(screenSize)
            }
        }
        val requester = remember { FocusRequester() }
        Gemini(Modifier.fillMaxSize()
            .focusRequester(requester)
            .focusable()
            .onKeyEvent {
                when (it.type) {
                    KeyEventType.KeyDown -> keys.add(it.key)
                    KeyEventType.KeyUp -> keys.remove(it.key)
                    else -> Unit
                }
//                println(keys)
                true
            }.onPointerEvent(PointerEventType.Press) {
                if (it.buttons.isPrimaryPressed) pressed.add(0)
//                println(pressed)
            }.onPointerEvent(PointerEventType.Release) {
                if (!it.buttons.isPrimaryPressed) pressed.remove(0)
//                println(pressed)
            }) {
            scene = game
        }
        LaunchedEffect(requester) {
            requester.requestFocus()
        }
    }
}

fun main() = singleWindowApplication(title = "Gemini Asteroids") {
    AsteroidsGame()
}
