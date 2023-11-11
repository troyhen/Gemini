package examples

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.singleWindowApplication
import examples.checkers.Checkers
import kotlinx.coroutines.flow.MutableStateFlow

fun main() = singleWindowApplication(title = "Gemini Checkers") {
    val pointerFlow = remember { MutableStateFlow<Offset?>(null) }
    MaterialTheme {
        Checkers(
            pointerFlow = pointerFlow,
            modifier = Modifier.pointerInput(PointerEventPass.Main) {
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
