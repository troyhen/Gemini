package gemini

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.resourcesFetcher

@Composable
fun Gemini(modifier: Modifier = Modifier, builder: suspend SceneScope.() -> Unit) {
    val textMeasurer = rememberTextMeasurer()
    var stage by remember { mutableStateOf(Stage(textMeasurer)) }
    val desktopConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        // Available only on Desktop.
        resourcesFetcher()
        // Available only on Desktop.
        // An alternative svg decoder
//        batikSvgDecoder()
    }
    CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
        Canvas(modifier) {
            stage.run {
                draw()
            }
        }
    }
    LaunchedEffect(builder) {
        stage = SceneScope(textMeasurer).run {
            builder()
            build()
        }
    }
    DisposableEffect(stage) {
        stage.start()
        onDispose {
            stage.stop()
        }
    }
}
