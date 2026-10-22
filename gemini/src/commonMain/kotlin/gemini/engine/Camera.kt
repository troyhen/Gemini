package gemini.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.scale
import gemini.geometry.*

class Camera {
    private val matrix: Matrix = Matrix()
    private var noTransform = true
    private var useTarget = false
    val target: Location = Location()
    val visible: World = World()
    val world: World = World()

    fun default() {
        noTransform = true
        useTarget = false
        target.set(0f, 0f, 0f)
        matrix.reset()
    }

    fun lookAt(x: Float, y: Float, z: Float = 0f) {
        useTarget = true
        target.set(x, y, z)
        if (noTransform && visible.width > 2f) {
            val halfWidth = visible.width * .5f
            val halfHeight = visible.height * .5f
            visible.set(x - halfWidth, x + halfWidth, y - halfHeight, y + halfHeight)
        }
    }

    fun lookAt(offset: Offset, z: Float = 0f) {
        lookAt(offset.x, offset.y, z)
    }

    fun lookAt(location: Location) {
        lookAt(location.x, location.y, location.z)
    }

    fun toWorld(screenOffset: Offset): Offset {
        if (noTransform) {
            if (!useTarget || visible.width <= 2f) return screenOffset
            return Offset(
                x = screenOffset.x + target.x - visible.width * .5f,
                y = screenOffset.y + target.y - visible.height * .5f,
            )
        }
        return screenOffset
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

    fun setFrom(camera: Camera) {
        noTransform = camera.noTransform
        useTarget = camera.useTarget
        target.setFrom(camera.target)
        matrix.setFrom(camera.matrix)
        visible.setFrom(camera.visible)
        world.setFrom(camera.world)
    }

    fun DrawTransform.transform() {
        if (noTransform) {
            if (useTarget) {
                val center = center
                visible.set(
                    target.x - center.x,
                    target.x + center.x,
                    target.y - center.y,
                    target.y + center.y,
                )
                translate(center.x - target.x, center.y - target.y)
            } else {
                visible.set(0f, size.width, 0f, size.height)
            }
            return
        }
        val size = size
        val world = world
        if (size.width == size.height) {
            visible.setFrom(world)
        } else if (size.width > size.height) {
            val y = .5f * (world.bottom + world.top)
            val s2 = .5f * size.height / size.width
            val h2 = s2 * (world.bottom - world.top)
            visible.set(world.left, world.right, y - h2, y + h2, world.near, world.far)
        } else {
            val x = .5f * (world.right + world.left)
            val s2 = .5f * size.width / size.height
            val w2 = s2 * (world.right - world.left)
            visible.set(x - w2, x + w2, world.top, world.bottom, world.near, world.far)
        }
        if (useTarget) {
            visible.set(
                visible.left + target.x,
                visible.right + target.x,
                visible.top + target.y,
                visible.bottom + target.y,
                visible.near + target.z,
                visible.far + target.z,
            )
        }
        val center = center
        translate(center.x, center.y)
        scale(center.max, Offset.Zero)
        transform(matrix)
        if (useTarget) {
            translate(-target.x, -target.y)
        }
    }
}

interface BeforeCamera
interface AfterCamera
