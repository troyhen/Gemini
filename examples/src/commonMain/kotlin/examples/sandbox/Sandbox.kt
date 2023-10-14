package examples.sandbox

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import gemini.engine.Gemini
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.frameRate
import gemini.foundation.sprite
import gemini.geometry.degrees
import io.kamel.image.asyncPainterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Sandbox() {
    val density = LocalDensity.current.density
    val symbol = asyncPainterResource("https://sarahscoop.com/wp-content/uploads/2023/03/gemini-ascendant-man-1.jpg").also {
        println("2 $it")
    }
    var mouse by remember { mutableStateOf(Offset(0f, 0f)) }
    var pressed by remember { mutableStateOf(false) }
    val basicDemo = rememberScene {
        camera.default()
        background(Color.Black)
        sprite(symbol, 100f, 100f, 100f, 130f) { time ->
            val rotation = -time.inWholeMilliseconds / 100f
            position.rotation.rotate(rotation.degrees)
        }
        rectangle(10f, 10f, 100f, 20f, Color.Red) { time ->
            val offset = time.inWholeMilliseconds / 100f
            position.location.move(offset, offset)
            position.rotation.rotate(offset.degrees)
        }
        circle(20f, Color.DarkGray) {
            position.location.set(mouse.x / density, mouse.y / density)
            color = if (pressed) Color.Cyan else Color.DarkGray
        }
        frameRate(Color.White)
    }
    Gemini(Modifier.fillMaxSize().pointerInput(PointerEventPass.Main) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                when (event.type) {
                    PointerEventType.Move -> mouse = event.changes.last().position
                    PointerEventType.Press -> pressed = true
                    PointerEventType.Release -> pressed = false
                }
            }
        }
    }) {
        scene = basicDemo
    }
}
