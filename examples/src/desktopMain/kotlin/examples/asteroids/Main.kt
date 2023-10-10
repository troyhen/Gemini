package examples.asteroids

import androidx.compose.ui.window.singleWindowApplication
import examples.astroids.AsteroidsGame

fun main() = singleWindowApplication(title = "Gemini Asteroids") {
    AsteroidsGame()
}
