package gemini

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer

@Composable
fun Gemini(modifier: Modifier = Modifier, builder: SceneScope.() -> Unit) {
    val textMeasurer = rememberTextMeasurer()
    val stage = remember(builder) {
        SceneScope(textMeasurer).run {
            builder()
            build()
        }
    }
    Canvas(modifier) {
        stage.run {
            draw()
        }
    }
    DisposableEffect(stage) {
        stage.start()
        onDispose {
            stage.stop()
        }
    }
}
