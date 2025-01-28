package examples

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.singleWindowApplication
import examples.beads.BeadGame


fun main() = singleWindowApplication(title = "Gemini Beads") {
    MaterialTheme {
        BeadGame()
    }
}
