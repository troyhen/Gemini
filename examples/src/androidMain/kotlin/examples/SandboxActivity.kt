package examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import examples.sandbox.Sandbox
import gemini.engine.Stage

class SandboxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold { padding ->
                    Sandbox(Modifier.padding(padding))
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
