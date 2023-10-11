package examples.asteroids

import androidx.compose.foundation.focusable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.window.singleWindowApplication
import examples.astroids.Control
import examples.astroids.Game
import examples.astroids.State

fun main() = singleWindowApplication(title = "Gemini Asteroids") {
    val state = remember { State() }
    val requester = remember { FocusRequester() }
    Game(
        state = state,
        modifier = Modifier.focusRequester(requester)
            .focusable()
            .onKeyEvent {
                when (it.type) {
                    KeyEventType.KeyDown -> when (it.key) {
                        Key.DirectionUp -> state.add(Control.GoForward)
                        Key.DirectionDown -> state.add(Control.GoBackward)
                        Key.DirectionLeft -> state.add(Control.TurnLeft)
                        Key.DirectionRight -> state.add(Control.TurnRight)
                        Key.Spacebar -> state.add(Control.Fire)
                    }

                    KeyEventType.KeyUp -> when (it.key) {
                        Key.DirectionUp -> state.remove(Control.GoForward)
                        Key.DirectionDown -> state.remove(Control.GoBackward)
                        Key.DirectionLeft -> state.remove(Control.TurnLeft)
                        Key.DirectionRight -> state.remove(Control.TurnRight)
                        Key.Spacebar -> state.remove(Control.Fire)
                    }
                }
                true
            }/*.pointerInput(PointerEventPass.Main) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Press -> if (event.buttons.isPrimaryPressed) pressed.add(0)
                            PointerEventType.Release -> if (!event.buttons.isPrimaryPressed) pressed.remove(0)
                        }
                    }
                }
            }*/
    )
    LaunchedEffect(requester) {
        requester.requestFocus()
        requester.captureFocus()
    }
}
