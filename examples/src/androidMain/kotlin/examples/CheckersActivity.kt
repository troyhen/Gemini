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
import examples.checkers.Checkers
import gemini.engine.pointerDrag
import kotlinx.coroutines.flow.MutableStateFlow

class CheckersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pointerFlow = remember { MutableStateFlow<Offset?>(null) }
            MaterialTheme {
                Scaffold { padding ->
                    Checkers(
                        pointerFlow = pointerFlow,
                        modifier = Modifier
                            .padding(padding)
                            .pointerDrag(pointerFlow),
                    )
                }
            }
        }
    }
}
