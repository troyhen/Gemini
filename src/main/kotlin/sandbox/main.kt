package sandbox

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.singleWindowApplication
import gemini.*
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.resourcesFetcher

fun main() = singleWindowApplication(title = "Gemini Sandbox") {
    val desktopConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        // Available only on Desktop.
        resourcesFetcher()
        // Available only on Desktop.
        // An alternative svg decoder
//        batikSvgDecoder()
    }
    CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
        val symbol = asyncPainterResource("https://sarahscoop.com/wp-content/uploads/2023/03/gemini-ascendant-man-1.jpg").also {
            println("2 $it")
        }
        val basicDemo = rememberScene {
            background(Color.Black)
            sprite(symbol, 100f, 100f, 100f, 130f) { time ->
                val rotation = -time.inWholeMilliseconds / 100f
                position.rotation.rotate(rotation.degrees)
            }
            rectangle(10f, 10f, 100f, 20f, Color.Red) { time ->
                val offset = time.inWholeMilliseconds / 100f
                position.location.move(offset, offset)
                position.rotation.rotate(offset.degrees)
            }
            frameRate(Color.White)
        }
        Gemini(Modifier.fillMaxSize()) {
            scene = basicDemo
        }
    }
}
