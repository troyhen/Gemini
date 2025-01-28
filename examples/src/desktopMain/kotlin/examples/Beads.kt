package examples

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import examples.beads.BeadGame


fun main() = singleWindowApplication(state = WindowState(width = 1200.dp, height = 1000.dp), title = "Gemini Beads") {
    MaterialTheme {
        BeadGame()
    }
}
