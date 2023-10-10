package examples.starfield

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.key.*
import androidx.compose.ui.window.singleWindowApplication
import gemini.*
import gemini.engine.Gemini
import gemini.engine.SceneScope
import gemini.engine.rememberScene
import gemini.foundation.MovingThing
import gemini.foundation.background
import gemini.foundation.frameRate
import gemini.geometry.Velocity
import kotlin.time.Duration

val cameraVelocity = Velocity(0f, 0f, -1f)

private class Star : MovingThing() {
    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        val seconds = elapsed.inSeconds
        position.location.move(cameraVelocity.xs * seconds, cameraVelocity.ys * seconds, cameraVelocity.zs * seconds)
    }

    override fun DrawScope.orientAndDraw() {
        val z = position.location.z.coerceAtLeast(MIN_Z)
        val offset = (position.location.offset / z).remap(size)
        val r0 = size.min / (z * 100)
        val r = r0.coerceAtLeast(1f)
        val color = Color.White.copy(alpha = r0.coerceAtMost(1f))
        drawCircle(color, r, offset)
        if (offset.x < -size.width || offset.x > size.width * 2 || offset.y < -size.height || offset.y > size.height * 2 || position.location.z < MIN_Z || position.location.z > MAX_Z) {
            restart()
        }
    }

    private fun restart() {
        val z = (MAX_Z - MIN_Z).random + MIN_Z
        position.location.set(2f.randomPlus() * z, 2f.randomPlus() * z, z)
    }

    companion object {
        private const val MIN_Z = .01f
        private const val MAX_Z = 16f
    }
}

fun SceneScope.starField(number: Int) {
    repeat(number) {
        add(Star())
    }
}

@Composable
fun StarField(modifier: Modifier = Modifier) {
    val keys = remember { mutableSetOf<Key>() }
    val stars = rememberScene {
        background(Color.Black)
        starField(100)
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
        }) {
        scene = stars
    }
    LaunchedEffect(requester) {
        requester.requestFocus()
    }
}

private fun detectKeys(keys: Set<Key>) {
    cameraVelocity.set(0f, 0f, 0f)
    if (Key.Enter in keys) cameraVelocity.zs = -1f
    if (Key.Tab in keys) cameraVelocity.zs = 1f
    if (Key.DirectionUp in keys) cameraVelocity.ys = -1f
    if (Key.DirectionDown in keys) cameraVelocity.ys = 1f
    if (Key.DirectionLeft in keys) cameraVelocity.xs = -1f
    if (Key.DirectionRight in keys) cameraVelocity.xs = 1f
//    println("$keys $cameraVelocity")
}
