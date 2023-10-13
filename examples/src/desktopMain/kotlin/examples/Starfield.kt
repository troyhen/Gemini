package examples

import androidx.compose.ui.window.singleWindowApplication
import examples.starfield.StarField

fun main() = singleWindowApplication(title = "Gemini Star Field") {
    StarField()
}
