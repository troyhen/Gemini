package examples.gravity

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import gemini.engine.Gemini
import gemini.engine.HandlePointer
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.frameRate
import gemini.geometry.Pivot
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Gravity(pointerFlow: StateFlow<Offset?>, modifier: Modifier = Modifier) {
    val system = remember { PlanetSystem() }

    Box(modifier) {
        val game = rememberScene {
            system.clear()
            camera.default()
            background(Color.Black)
            frameRate(Color.White, Pivot.NorthEast)
            orbitingPlanets(system)
        }
        Gemini {
            scene = game
        }
    }

    HandlePointer(
        pointerFlow = pointerFlow,
        drop = system::drop,
        drag = system::drag,
    )
}
