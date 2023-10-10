package gemini.engine

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer

@Composable
fun Gemini(modifier: Modifier = Modifier, builder: suspend SceneScope.() -> Unit) {
    val textMeasurer = rememberTextMeasurer()
    val stage = remember { Stage(textMeasurer) }
    Canvas(modifier) {
        stage.run {
            draw()
        }
    }
    LaunchedEffect(builder) {
        val scene = SceneScope().run {
            builder()
            build()
        }
        stage.load(scene)
    }
    DisposableEffect(stage) {
        stage.start()
        onDispose {
            stage.stop()
        }
    }
}
