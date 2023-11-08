package examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import examples.action.Town
import gemini.asset.Sound
import gemini.asset.asSource
import gemini.engine.Stage

class TownActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val desktopConfig = KamelConfig {
//            takeFrom(KamelConfig.Default)
//            httpFetcher {
//                Logging {
//                    level = LogLevel.INFO
//                    logger = Logger.SIMPLE
//                }
//            }
//            resourcesFetcher(this@TownActivity)
//        }
        Sound.context = this
        setContent {
//            CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
            MaterialTheme {
                Scaffold { padding ->
                    Town("asset:action/professor_walk_cycle_no_hat.png".asSource, Modifier.padding(padding))
                }
            }
//            }
        }
    }

    override fun onPause() {
        super.onPause()
        Stage.instance?.stop()
    }

    override fun onResume() {
        super.onResume()
        Stage.instance?.start()
    }
}
