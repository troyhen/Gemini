package examples.sandbox

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.singleWindowApplication
import gemini.engine.Gemini
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.frameRate
import gemini.foundation.sprite
import gemini.geometry.degrees
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.resourcesFetcher

@OptIn(ExperimentalComposeUiApi::class)
fun main() = singleWindowApplication(title = "Gemini Sandbox") {
    val desktopConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        // Available only on Desktop.
        resourcesFetcher()
        // Available only on Desktop.
        // An alternative svg decoder
//        batikSvgDecoder()
    }
    val density = LocalDensity.current.density
    CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
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
        Gemini(Modifier.fillMaxSize().onPointerEvent(PointerEventType.Move) {
            mouse = it.changes.last().position
        }.onPointerEvent(PointerEventType.Press) {
            pressed = true
        }.onPointerEvent(PointerEventType.Release) {
            pressed = false
        }) {
            scene = basicDemo
        }
    }
}
