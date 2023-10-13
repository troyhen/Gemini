package gemini.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.scale
import gemini.geometry.World
import gemini.geometry.max
import gemini.geometry.orthographic
import gemini.geometry.perspective

class Camera {
    private val matrix: Matrix = Matrix()
    private var noTransform = true
    val visible: World = World()
    val world: World = World()

    fun default() {
        noTransform = true
        matrix.reset()
    }

    fun perspective(left: Float = -1f, right: Float = 1f, top: Float = -1f, bottom: Float = 1f, near: Float = -1f, far: Float = 1f) {
        noTransform = false
        matrix.perspective(left, right, top, bottom, near, far)
        world.set(left, right, top, bottom, near, far)
    }

    fun orthographic(left: Float = -1f, right: Float = 1f, top: Float = -1f, bottom: Float = 1f, near: Float = -1f, far: Float = 1f) {
        noTransform = false
        matrix.orthographic(left, right, top, bottom, near, far)
        world.set(left, right, top, bottom, near, far)
    }

    fun DrawTransform.reverse() {
        if (noTransform) return
        val center = center
        scale(1 / center.max, Offset.Zero)
        translate(-center.x, -center.y)
    }

    fun setFrom(camera: Camera) {
        noTransform = camera.noTransform
        matrix.setFrom(camera.matrix)
        visible.setFrom(camera.visible)
        world.setFrom(camera.world)
    }

    fun DrawScope.transform() {
        if (noTransform) {
            visible.set(0f, size.width, 0f, size.height)
            return
        }
        val size = size
        val world = Stage.world
        if (size.width == size.height) {
            Stage.visible.setFrom(world)
        } else if (size.width > size.height) {
            val y = .5f * (world.bottom + world.top)
            val s2 = .5f * size.height / size.width
            val h2 = s2 * (world.bottom - world.top)
            Stage.visible.set(world.left, world.right, y - h2, y + h2, world.near, world.far)
        } else {
            val x = .5f * (world.right + world.left)
            val s2 = .5f * size.width / size.height
            val w2 = s2 * (world.right - world.left)
            Stage.visible.set(x - w2, x + w2, world.top, world.bottom, world.near, world.far)
        }
        val center = center
        drawContext.transform.translate(center.x, center.y)
        drawContext.transform.scale(center.max, Offset.Zero)
        drawContext.transform.transform(matrix)
    }
}
