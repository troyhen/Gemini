package sandbox

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.singleWindowApplication
import gemini.Gemini
import gemini.background

fun main() = singleWindowApplication(title = "Gemini Sandbox") {
    Gemini(Modifier.fillMaxSize()) {
        background(Color.Black)
        rectangle(0f, 0f, 20f, 20f, Color.Red) { time ->
            val offset = time.inWholeMilliseconds / 100f
            orientation.location.move(offset, offset)
        }
    }
}
