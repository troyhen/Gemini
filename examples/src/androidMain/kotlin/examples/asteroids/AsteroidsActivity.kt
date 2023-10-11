package examples.asteroids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import examples.astroids.Game
import examples.astroids.State

class AsteroidsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val state = State()
        setContent {
            MaterialTheme {
                Scaffold {
                    Game(state, Modifier.padding(it))
                }
            }
        }
    }
}