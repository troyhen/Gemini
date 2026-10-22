package examples

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.singleWindowApplication
import examples.checkers.Checkers
import gemini.engine.pointerDrag
import kotlinx.coroutines.flow.MutableStateFlow

fun main() = singleWindowApplication(title = "Gemini Checkers") {
    val pointerFlow = remember { MutableStateFlow<Offset?>(null) }
    MaterialTheme {
        Checkers(
            pointerFlow = pointerFlow,
            modifier = Modifier.pointerDrag(pointerFlow),
        )
    }
}
