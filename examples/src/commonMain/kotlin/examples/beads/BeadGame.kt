package examples.beads

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import examples.checkers.Checker
import examples.checkers.CheckerBoard
import gemini.engine.Gemini
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.frameRate
import gemini.geometry.*
import kotlinx.coroutines.flow.StateFlow

@Composable
fun BeadGame(pointerFlow: StateFlow<Offset?>, modifier: Modifier = Modifier) {

    var iteration by remember { mutableStateOf(0) }
    var showRestart by remember { mutableStateOf(false) }
    var dragging: Bead? = null
    val beads = mutableListOf<Bead>()
    val rectangle = Rectangle()

    fun onEnd() {
        showRestart = true
    }

    fun onRestart() {
        showRestart = false
        iteration++
        Stage.instance?.start()
    }

    Box(modifier) {
        val game = rememberScene(iteration) {
            camera.default()
            background(Color.Black)
            frameRate(Color.White, Pivot.NorthEast)
            val main = bowl(Location(450f, 450f), 250f, Color.DarkGray)
            val step = 360.degrees / BOWLS
            repeat(BOWLS) {
                val angle = step * it
                bowl(main.position.location + Offset(350f * angle.cos(), 350f * angle.sin()), 100f, Color.Gray)
            }
            colors.forEach { color ->
                repeat(BEADS) {
                    beads.add(bead(main.position.location + Offset(main.radius, main.radius).randomPlus(), color))
                }
            }
        }
        Gemini {
            scene = game
        }
        if (showRestart) {
            TextButton(
                onClick = ::onRestart,
                modifier = Modifier.align(Alignment.Center),
                colors = ButtonDefaults.textButtonColors(containerColor = Color.DarkGray.copy(alpha = .5f)),
            ) {
                Text("Restart", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
//    fun Bead.isHit(offset: Offset): Boolean {
//        return rectangle.contains(offset).also {
//            if (it) {
//                dragging = offset
//            }
//        }
//    }
//    HandlePointer(pointerFlow, { offset ->
//        dragging?.position?.location?.setFrom(Location(offset))
//    }, { offset ->
//        offset ?: return@HandlePointer
//        if (dragging == null) {
//            dragging = beads.find {
//                it.position.rectangle(rectangle)
//            }
//        }
//        dragging?.position?.location?.set(offset.x, offset.y)
//    })
}

@Composable
private fun HandlePointer(pointerFlow: StateFlow<Offset?>, drop: (Offset) -> Unit, drag: (Offset?) -> Unit) {
    var lastPointer by remember { mutableStateOf<Offset?>(null) }
    val pointer = pointerFlow.collectAsState().value
    LaunchedEffect(pointer) {
        if (pointer == null) {
            lastPointer?.let { drop(it) }
        } else {
            drag(pointer)
        }
        lastPointer = pointer
    }
}

fun SceneScope.checkerBoard(): CheckerBoard {
    return CheckerBoard().also {
        add(it)
    }
}

private const val BOWLS = 6
private const val BEADS = 15
private val colors = listOf(Color.Red, Color.Blue, Color.Yellow, Color.Cyan, Color.Green, Color.Magenta)