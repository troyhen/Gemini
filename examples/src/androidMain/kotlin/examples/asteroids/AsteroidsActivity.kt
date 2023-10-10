package examples.asteroids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import examples.astroids.AsteroidsGame

class AsteroidsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AsteroidsGame()
        }
    }
}