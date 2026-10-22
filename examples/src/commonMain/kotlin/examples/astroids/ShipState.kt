package examples.astroids

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.vector.ImageVector

class ShipState {
    val controls = mutableStateListOf<Control>()

    fun add(control: Control) {
        if (control !in controls) {
            controls.add(control)
        }
    }

    fun remove(control: Control) {
        controls.remove(control)
    }
}

enum class Control(val contentDescription: String, val icon: ImageVector) {
    Exit("Quit", Icons.AutoMirrored.Outlined.ArrowBack),
    Fire("Fire", Icons.Outlined.RadioButtonChecked),
    GoForward("Forward", Icons.Outlined.KeyboardArrowUp),
    GoBackward("Backward", Icons.Outlined.KeyboardArrowDown),
    TurnLeft("Turn Left", Icons.AutoMirrored.Outlined.KeyboardArrowLeft),
    TurnRight("Turn Right", Icons.AutoMirrored.Outlined.KeyboardArrowRight),
}
