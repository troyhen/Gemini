package sandbox

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.singleWindowApplication
import gemini.Gemini
import gemini.background
import gemini.frameRate

fun main() = singleWindowApplication(title = "Gemini Sandbox") {
    Gemini(Modifier.fillMaxSize()) {
        background(Color.Black)
        rectangle(0f, 0f, 20f, 20f, Color.Red) { rect, time ->
            val offset = time.inWholeMilliseconds / 100f
            rect.orientation.location.move(offset, offset)
        }
        frameRate(Color.White)
    }
}
