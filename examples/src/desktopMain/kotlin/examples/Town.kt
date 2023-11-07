package examples

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.singleWindowApplication
import examples.action.Town
import gemini.asset.asSource
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.resourcesFetcher
import io.ktor.client.plugins.logging.*

fun main() = singleWindowApplication(title = "Town") {
    val desktopConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        httpFetcher {
            Logging {
                level = LogLevel.INFO
                logger = Logger.SIMPLE
            }
        }
        resourcesFetcher()
    }
    CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
        Town("resource:sandbox/professor_walk_cycle_no_hat.png".asSource)
    }
}
