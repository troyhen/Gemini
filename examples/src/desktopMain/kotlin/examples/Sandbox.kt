package examples

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.singleWindowApplication
import examples.sandbox.Sandbox
import gemini.asset.asSource
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.resourcesFetcher

fun main() = singleWindowApplication(title = "Gemini Sandbox") {
    val desktopConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        resourcesFetcher()
    }
    CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
        Sandbox("resource:sandbox/click.wav".asSource)
    }
}
