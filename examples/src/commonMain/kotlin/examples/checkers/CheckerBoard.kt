package examples.checkers

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import gemini.engine.BeforeCamera
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.foundation.Thing

class CheckerBoard : Thing(), BeforeCamera {

    private val checkers = mutableListOf<Checker>()
    private var dragging: Checker? = null
    private var grid: Float = 0f
    private var inset: Float = 0f

    private fun clear() {
        val stage = Stage.instance ?: return
        checkers.forEach {
            stage.remove(it)
        }
        checkers.clear()
    }

    fun drag(offset: Offset) {
        if (dragging == null) {
            dragging = checkers.find { it.isHit(offset) }
        }
        dragging?.drag(offset)
    }

    override fun DrawScope.draw() {
        grid = size.minDimension / 9
        inset = grid / 2
        drawRect(Color.White)
        drawRect(Color.DarkGray, Offset(inset, inset), Size(grid, grid) * 8f)
        for (row in 0..7) {
            for (col in 0..7) {
                if ((row + col) % 2 == 0) continue
                drawRect(Color.Red, Offset(grid * col + inset, grid * row + inset), Size(grid, grid))
            }
        }
    }

    fun drop(offset: Offset) {
        val boardPosition = Offset(offset.x / grid - 1, offset.y / grid - 1).round()
        dragging?.drop(boardPosition)
        dragging = null
        Stage.instance?.step()
    }

    fun start() {
        clear()
        Stage.instance?.run {
            for (side in Side.entries) {
                repeat(12) { piece ->
                    val row = piece / 4 + if (side == Side.Black) 5 else 0
                    val column = 2 * (piece % 4) + row % 2
                    checkers.add(piece(side, IntOffset(column, row)))
                }
            }
        }
    }
}

fun SceneScope.checkerBoard(): CheckerBoard {
    return CheckerBoard().also {
        add(it)
    }
}
