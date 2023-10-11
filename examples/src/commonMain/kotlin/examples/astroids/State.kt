package examples.astroids

import androidx.compose.runtime.mutableStateListOf

enum class Control {
    GoForward, GoBackward, TurnRight, TurnLeft, Fire
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
