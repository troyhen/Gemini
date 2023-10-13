package examples

import androidx.compose.ui.window.singleWindowApplication
import examples.sandbox.Sandbox

fun main() = singleWindowApplication(title = "Gemini Sandbox") {
//    val desktopConfig = KamelConfig {
//        takeFrom(KamelConfig.Default)
//        // Available only on Desktop.
//        resourcesFetcher()
//        // Available only on Desktop.
//        // An alternative svg decoder
////        batikSvgDecoder()
//    }
//    CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
    Sandbox()
//    }
}
