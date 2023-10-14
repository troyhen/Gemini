package examples

import androidx.compose.foundation.focusable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.window.singleWindowApplication
import examples.astroids.Control
import examples.astroids.Game
import examples.astroids.ShipState

fun main() = singleWindowApplication(title = "Gemini Asteroids") {
    val shipState = remember { ShipState() }
    val requester = remember { FocusRequester() }
    MaterialTheme {
        Game(
            shipState = shipState,
            modifier = Modifier.focusRequester(requester)
                .focusable()
                .onKeyEvent {
                    when (it.type) {
                        KeyEventType.KeyDown -> when (it.key) {
                            Key.DirectionUp -> shipState.add(Control.GoForward)
                            Key.DirectionDown -> shipState.add(Control.GoBackward)
                            Key.DirectionLeft -> shipState.add(Control.TurnLeft)
                            Key.DirectionRight -> shipState.add(Control.TurnRight)
                            Key.Spacebar -> shipState.add(Control.Fire)
                            Key.Escape -> shipState.add(Control.Exit)
                        }

                        KeyEventType.KeyUp -> when (it.key) {
                            Key.DirectionUp -> shipState.remove(Control.GoForward)
                            Key.DirectionDown -> shipState.remove(Control.GoBackward)
                            Key.DirectionLeft -> shipState.remove(Control.TurnLeft)
                            Key.DirectionRight -> shipState.remove(Control.TurnRight)
                            Key.Spacebar -> shipState.remove(Control.Fire)
                            Key.Escape -> shipState.remove(Control.Exit)
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
    }
    LaunchedEffect(requester) {
        requester.requestFocus()
        requester.captureFocus()
    }
}
