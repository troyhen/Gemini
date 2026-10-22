package examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import examples.beads.BeadGame
import gemini.engine.Stage
import gemini.engine.pointerDrag
import kotlinx.coroutines.flow.MutableStateFlow

class BeadsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pointerFlow = remember { MutableStateFlow<Offset?>(null) }
            MaterialTheme {
                Scaffold { padding ->
                    BeadGame(
                        pointerFlow = pointerFlow,
                        modifier = Modifier
                            .padding(padding)
                            .pointerDrag(pointerFlow),
                    )
                }
            }
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
