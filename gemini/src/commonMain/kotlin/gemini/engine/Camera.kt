package gemini.engine

import androidx.compose.ui.graphics.Matrix

class Camera {
    val matrix: Matrix = Matrix()

    fun perspective(left: Float = -1f, right: Float = -1f, top: Float = 1f, bottom: Float = 1f, near: Float = -1f, far: Float = 1f) {
        val n2 = 2 * near
        val rml = right - left
        val bmt = bottom - top
        val fmn = far - near
        matrix.reset()
        matrix[0, 0] = n2 / rml
        matrix[0, 3] = -near * (right + left) / rml
        matrix[1, 1] = n2 / bmt
        matrix[1, 3] = -near * (bottom + top) / bmt
        matrix[2, 2] = (far + near) / fmn
        matrix[2, 3] = -far * n2 / fmn
        matrix[3, 2] = 1f
        matrix[3, 3] = 0f
    }

    fun orthographic(left: Float = -1f, right: Float = 1f, top: Float = -1f, bottom: Float = 1f, near: Float = -1f, far: Float = 1f) {
        val rml = right - left
        val bmt = bottom - top
        val fmn = far - near
        matrix.reset()
        matrix[0, 0] = 2 / rml
        matrix[0, 3] = -(right + left) / rml
        matrix[1, 1] = 2 / bmt
        matrix[1, 3] = -(bottom + top) / bmt
        matrix[2, 2] = 2 / fmn
        matrix[2, 3] = -(far + near) / fmn
    }
}
