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
import gemini.inSeconds
import gemini.min
import gemini.random
import gemini.randomPlus
import kotlin.time.Duration

private class Star(private val size: Size) : MovingThing() {
    //    private val last = Location()
    override suspend fun act(elapsed: Duration) {
        super.act(elapsed)
        position.location.z -= 10 * elapsed.inSeconds
    }

    override fun DrawScope.orientAndDraw() {
        val z = position.location.z.coerceAtLeast(1f)
        val offset = position.location.offset - center
        val x = offset.x / z + center.x
        val y = offset.y / z + center.y
        val r0 = size.min / (z * 5)
        val r = r0.coerceAtLeast(1f)
        val color = Color.White.copy(alpha = r0.coerceAtMost(1f))
//        drawLine(Color.LightGray, Offset(last.x, last.y), Offset(x, y), r / 2)
        drawCircle(color, r, Offset(x, y))
//        last.set(x, y, z)
        if (x < 0 || x > size.width || y < 0 || y > size.height || position.location.z < 1) {
            restart()
        }
    }

    private fun restart() {
        val z = 200f.random + 200f
        position.location.set(size.width.randomPlus() * z, size.height.randomPlus() * z, z)
//        last.set(position.location)
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
                    KeyEventType.KeyDown -> keys.add(it.key)
                    KeyEventType.KeyUp -> keys.remove(it.key)
                    else -> Unit
                }
//                println(keys)
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

fun main() = singleWindowApplication(title = "Gemini Star Field") {
    StarField()
}
