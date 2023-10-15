package examples.sandbox

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import gemini.asset.Sound
import gemini.asset.Source
import gemini.asset.SourceUrl
import gemini.engine.Gemini
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.frameRate
import gemini.foundation.sprite
import gemini.geometry.degrees
import kotlinx.coroutines.launch
import java.net.URL

@Composable
fun Sandbox(clickSource: Source, modifier: Modifier = Modifier) {
    val density = LocalDensity.current.density
    var clickSound: Sound? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    var mouse by remember { mutableStateOf(Offset(0f, 0f)) }
    var pressed by remember { mutableStateOf(false) }
    val basicDemo = rememberScene {
        clickSound = sound(clickSource)
        camera.default()
        background(Color.Black)
        val geminiImage = image(SourceUrl(URL("https://sarahscoop.com/wp-content/uploads/2023/03/gemini-ascendant-man-1.jpg")))
        sprite(geminiImage, 100f, 100f, 100f, 130f) { time ->
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
    Gemini(modifier.fillMaxSize().pointerInput(PointerEventPass.Main) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                when (event.type) {
                    PointerEventType.Move -> mouse = event.changes.last().position
                    PointerEventType.Press -> {
                        pressed = true
                        scope.launch {
                            clickSound?.play()
                        }
                    }
                    PointerEventType.Release -> pressed = false
                }
            }
        }
    }) {
        scene = basicDemo
    }
}
