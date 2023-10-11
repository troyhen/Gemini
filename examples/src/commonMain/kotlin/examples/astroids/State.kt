package examples.astroids

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.vector.ImageVector

enum class Control(val contentDescription: String, val icon: ImageVector) {
    Exit("Quit", Icons.Outlined.ArrowBack),
    Fire("Fire", Icons.Outlined.RadioButtonChecked),
    GoForward("Forward", Icons.Outlined.KeyboardArrowUp),
    GoBackward("Backward", Icons.Outlined.KeyboardArrowDown),
    TurnLeft("Turn Left", Icons.Outlined.KeyboardArrowLeft),
    TurnRight("Turn Right", Icons.Outlined.KeyboardArrowRight),
}

class State {
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
