package examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import examples.starfield.StarField
import gemini.engine.Stage

class StarFieldActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold {
                    StarField(Modifier.padding(it))
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