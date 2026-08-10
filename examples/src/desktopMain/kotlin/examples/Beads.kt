package examples

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import examples.beads.BeadGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


fun main() = singleWindowApplication(state = WindowState(width = 1200.dp, height = 1000.dp), title = "Gemini Beads") {
    val pointerFlow = remember { MutableStateFlow<Offset?>(null) }
    MaterialTheme {
        BeadGame(
            pointerFlow,
            Modifier.pointerInput(PointerEventPass.Main) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val offset = event.changes.last().position
                        when (event.type) {
                            PointerEventType.Move -> if (event.buttons.isPrimaryPressed) pointerFlow.value = offset
                            PointerEventType.Press -> if (event.buttons.isPrimaryPressed) pointerFlow.value = offset
                            PointerEventType.Release -> if (!event.buttons.isPrimaryPressed) pointerFlow.value = null
                        }
                    }
                }
            }
        )
    }
}
