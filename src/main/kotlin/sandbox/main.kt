package sandbox

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.singleWindowApplication
import gemini.Gemini
import gemini.background
import gemini.degrees
import gemini.frameRate

fun main() = singleWindowApplication(title = "Gemini Sandbox") {
    Gemini(Modifier.fillMaxSize()) {
        background(Color.Black)
        rectangle(10f, 10f, 100f, 20f, Color.Red) { rect, time ->
            val offset = time.inWholeMilliseconds / 100f
            rect.orientation.location.move(offset, offset)
            rect.orientation.rotation.rotate(offset.degrees)
        }
        frameRate(Color.White)
    }
}
