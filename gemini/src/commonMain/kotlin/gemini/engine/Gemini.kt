package gemini.engine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import gemini.asset.SetupImages

@Composable
fun Gemini(modifier: Modifier = Modifier, startImmediately: Boolean = true, onStart: suspend Stage.() -> Unit = {}, builder: suspend SceneScope.() -> Unit) {
    val textMeasurer = rememberTextMeasurer()
    val scope = rememberCoroutineScope()
    val stage = remember { Stage(scope, textMeasurer, startImmediately) }
    SetupImages()

    Canvas(modifier.fillMaxSize()) {
        stage.run { draw() }
    }

    LaunchedEffect(builder) {
        val scene = SceneScope().run {
            builder()
            build()
        }
        stage.load(scene)
        onStart(stage)
    }
}
