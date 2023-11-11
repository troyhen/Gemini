package examples.checkers

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import gemini.engine.Gemini
import gemini.engine.rememberScene
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Checkers(pointerFlow: StateFlow<Offset?>, modifier: Modifier = Modifier) {
    var checkerBoard by remember { mutableStateOf<CheckerBoard?>(null) }
    val game = rememberScene {
        camera.default()
        checkerBoard = checkerBoard()
    }
    Gemini(modifier, startImmediately = false, onStart = { checkerBoard?.start() }) {
        scene = game
    }
    checkerBoard?.HandlePointer(pointerFlow)
}

@Composable
private fun CheckerBoard.HandlePointer(pointerFlow: StateFlow<Offset?>) {
    var lastPointer by remember { mutableStateOf<Offset?>(null) }
    val pointer = pointerFlow.collectAsState().value
    LaunchedEffect(pointer) {
        if (pointer == null) {
            lastPointer?.let { drop(it) }
        } else {
            drag(pointer)
        }
        lastPointer = pointer
    }
}
