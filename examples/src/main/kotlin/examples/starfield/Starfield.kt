package examples.starfield

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.window.singleWindowApplication
import gemini.engine.Gemini
import gemini.engine.SceneScope
import gemini.engine.rememberScene
import gemini.foundation.MovingThing
import gemini.foundation.background
import gemini.foundation.frameRate
import gemini.geometry.Velocity
import gemini.inSeconds
import gemini.min
import gemini.random
import gemini.randomPlus
import kotlin.time.Duration

val cameraVelocity = Velocity(0f, 0f, -10f)

private class Star(private val size: Size) : MovingThing() {
    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        val seconds = elapsed.inSeconds
        position.location.move(cameraVelocity.xs * seconds, cameraVelocity.ys * seconds, cameraVelocity.zs * seconds)
    }

    override fun DrawScope.orientAndDraw() {
        val z = position.location.z.coerceAtLeast(1f)
        val offset = position.location.offset - center
        val x = offset.x / z + center.x
        val y = offset.y / z + center.y
        val r0 = size.min / (z * 5)
        val r = r0.coerceAtLeast(1f)
        val color = Color.White.copy(alpha = r0.coerceAtMost(1f))
        drawCircle(color, r, Offset(x, y))
        if (x < 0 || x > size.width || y < 0 || y > size.height || position.location.z < 1 || position.location.z > MAX_Z) {
            restart()
        }
    }

    private fun restart() {
        val z = MAX_Z.random
        position.location.set(size.width.randomPlus() * z, size.height.randomPlus() * z, z)
    }

    companion object {
        private const val MAX_Z = 400f
    }
}

fun SceneScope.starField(number: Int, size: Size) {
    repeat(number) {
        add(Star(size))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StarField(modifier: Modifier = Modifier) {
    val keys = remember { mutableSetOf<Key>() }
    val pressed = remember { mutableSetOf<Int>() }
    BoxWithConstraints(modifier.fillMaxSize()) {
        val stars = rememberScene {
            background(Color.Black)
            starField(1000, Size(maxWidth.value, maxHeight.value))
            frameRate(Color.White)
        }
        val requester = remember { FocusRequester() }
        Gemini(modifier.fillMaxSize()
            .focusRequester(requester)
            .focusable()
            .onKeyEvent {
                when (it.type) {
                    KeyEventType.KeyDown -> {
                        keys.add(it.key)
                        detectKeys(keys)
                    }
                    KeyEventType.KeyUp -> keys.remove(it.key)
                    else -> Unit
                }
                true
            }.onPointerEvent(PointerEventType.Press) {
                if (it.buttons.isPrimaryPressed) pressed.add(0)
//                println(pressed)
            }.onPointerEvent(PointerEventType.Release) {
                if (!it.buttons.isPrimaryPressed) pressed.remove(0)
//                println(pressed)
            }) {
            scene = stars
        }
        LaunchedEffect(requester) {
            requester.requestFocus()
        }
    }
}

private fun detectKeys(keys: Set<Key>) {
    cameraVelocity.set(0f, 0f, 0f)
    if (Key.Enter in keys) cameraVelocity.zs = -10f
    if (Key.Tab in keys) cameraVelocity.zs = 10f
    if (Key.DirectionUp in keys) cameraVelocity.ys = -1000f
    if (Key.DirectionDown in keys) cameraVelocity.ys = 1000f
    if (Key.DirectionLeft in keys) cameraVelocity.xs = -1000f
    if (Key.DirectionRight in keys) cameraVelocity.xs = 1000f
//    println("$keys $cameraVelocity")
}

fun main() = singleWindowApplication(title = "Gemini Star Field") {
    StarField()
}
