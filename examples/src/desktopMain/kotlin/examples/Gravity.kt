package examples

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import examples.gravity.Gravity
import gemini.engine.pointerDrag
import kotlinx.coroutines.flow.MutableStateFlow

fun main() = singleWindowApplication(state = WindowState(width = 1200.dp, height = 1000.dp), title = "Gemini Gravity") {
    val pointerFlow = remember { MutableStateFlow<Offset?>(null) }
    MaterialTheme {
        Gravity(
            pointerFlow = pointerFlow,
            modifier = Modifier.pointerDrag(pointerFlow),
        )
    }
}
