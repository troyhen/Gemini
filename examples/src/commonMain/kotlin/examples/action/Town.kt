package examples.action

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import examples.action.Direction.*
import gemini.asset.Source
import gemini.engine.Gemini
import gemini.engine.Stage
import gemini.engine.rememberScene
import gemini.foundation.background
import gemini.foundation.cycle
import gemini.foundation.frameRate
import gemini.foundation.sprite
import gemini.geometry.Pivot
import gemini.geometry.random
import gemini.geometry.seconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun Town(tileMap: Source, modifier: Modifier = Modifier) {
    val town = rememberScene {
        camera.default()
        background(Color.Black)
        val atlas = tileMap(tileMap, IntSize(64, 64))
        val tiles = 0..7
        val north = cycle(1.seconds, atlas, tiles.map { IntOffset(it, 0) })
        val west = cycle(1.seconds, atlas, tiles.map { IntOffset(it, 1) })
        val south = cycle(1.seconds, atlas, tiles.map { IntOffset(it, 2) })
        val east = cycle(1.seconds, atlas, tiles.map { IntOffset(it, 3) })
        repeat(20) {
            var direction = Direction.random
            var face = when (direction) {
                South -> south
                West -> west
                North -> north
                East -> east
            }
            sprite({ face }, Offset(800f, 400f).random, Size(100f, 100f)) { elapsed ->
                val distance = 50 * elapsed.seconds
                when (direction) {
                    South -> {
                        position.location.move(0f, distance)
                        if (position.location.y > Stage.visible.height) {
                            direction = North
                            face = north
                        }
                    }

                    West -> {
                        position.location.move(-distance, 0f)
                        if (position.location.x < 0) {
                            direction = East
                            face = east
                        }
                    }

                    North -> {
                        position.location.move(0f, -distance)
                        if (position.location.y < 0) {
                            direction = South
                            face = south
                        }
                    }

                    East -> {
                        position.location.move(distance, 0f)
                        if (position.location.x > Stage.visible.width) {
                            direction = West
                            face = west
                        }
                    }
                }
            }
        }
        frameRate(Color.White, pivot = Pivot.SouthEast)
    }
    Gemini(modifier) {
        scene = town
    }
}

enum class Direction {
    South, West, North, East;

    companion object {
        val random: Direction get() = entries[entries.size.random]
    }
}