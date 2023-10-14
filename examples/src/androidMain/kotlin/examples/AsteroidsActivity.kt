package examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import examples.astroids.Control
import examples.astroids.Game
import examples.astroids.ShipState
import gemini.engine.Stage

class AsteroidsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shipState = ShipState()
        setContent {
            MaterialTheme {
                Scaffold { padding ->
                    Box(Modifier.padding(padding)) {
                        Game(shipState)
                        Control(shipState, Control.Exit, Modifier.align(Alignment.TopStart))
                        Control(shipState, Control.Fire, Modifier.align(Alignment.BottomStart))
                        SteeringControls(shipState, Modifier.align(Alignment.BottomEnd))
                    }
                }
            }
            ExitHandler(shipState)
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

    @Composable
    private fun ExitHandler(shipState: ShipState) {
        val exit = remember {
            derivedStateOf { Control.Exit in shipState.controls }
        }.value
        LaunchedEffect(exit) {
            if (exit) {
                finish()
            }
        }
    }

    @Composable
    private fun SteeringControls(shipState: ShipState, modifier: Modifier = Modifier) {
        Box(modifier) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Control(shipState, Control.GoForward)
                Row {
                    Control(shipState, Control.TurnLeft)
                    Spacer(Modifier.width(10.dp))
                    Control(shipState, Control.TurnRight)
                }
                Control(shipState, Control.GoBackward)
            }
        }
    }

    @Composable
    private fun Control(shipState: ShipState, control: Control, modifier: Modifier = Modifier) {
        val color = if (control in shipState.controls) Color.Gray else Color.DarkGray
        val modifier2 = modifier.pointerInput(shipState) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    when (event.type) {
                        PointerEventType.Press -> /*if (event.buttons.isPrimaryPressed)*/ shipState.controls.add(control)
                        PointerEventType.Release -> /*if (!event.buttons.isPrimaryPressed)*/ shipState.controls.remove(control)
                    }
                }
            }
        }.clip(CircleShape).background(color).padding(10.dp)
        Icon(control.icon, control.contentDescription, modifier2, tint = Color.Yellow)
    }
}
