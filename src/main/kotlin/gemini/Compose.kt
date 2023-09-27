package gemini

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Gemini(modifier: Modifier = Modifier, builder: GeminiScope.() -> Unit) {
    val scope = remember { GeminiScope() }
    val stage = remember(builder) {
        scope.run(builder)
        scope.build()
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
