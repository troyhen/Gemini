package gemini.engine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import gemini.asset.SetupImages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Gemini(modifier: Modifier = Modifier, startImmediately: Boolean = true, onStart: suspend Stage.() -> Unit = {}, builder: suspend SceneScope.() -> Unit) {
    val textMeasurer = rememberTextMeasurer()
    val scope = rememberCoroutineScope()
    val stage = remember { Stage(scope, textMeasurer, startImmediately) }
    SetupImages()

    Canvas(modifier.fillMaxSize()) {
        stage.run { draw() }
    }

    LaunchedEffect(builder) {
        val scene = SceneScope().run {
            builder()
            build()
        }
        stage.load(scene)
        onStart(stage)
    }

    DisposableEffect(stage) {
        onDispose {
            stage.dispose()
        }
    }
}

fun Modifier.pointerDrag(pointerFlow: MutableStateFlow<Offset?>): Modifier = pointerInput(PointerEventPass.Main) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            val offset = event.changes.last().position
            val isPressed = event.buttons.isPrimaryPressed || event.changes.any { it.pressed }
            when (event.type) {
                PointerEventType.Move -> if (isPressed) pointerFlow.value = offset
                PointerEventType.Press -> if (isPressed) pointerFlow.value = offset
                PointerEventType.Release -> if (!isPressed) pointerFlow.value = null
            }
        }
    }
}

@Composable
fun HandlePointer(pointerFlow: StateFlow<Offset?>, drop: (Offset) -> Unit, drag: (Offset) -> Unit) {
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
