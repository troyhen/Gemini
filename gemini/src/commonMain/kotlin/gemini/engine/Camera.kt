package gemini.engine

import androidx.compose.ui.graphics.Matrix
import gemini.geometry.Angle

class Camera {
    val matrix: Matrix = Matrix()

    fun lookAt(toX: Float, toY: Float, toZ: Float, angle: Angle = Angle()) {

    }

    fun orient(fromX: Float, fromY: Float, fromZ: Float, toX: Float, toY: Float, toZ: Float, angle: Angle = Angle()) {

    }
}
