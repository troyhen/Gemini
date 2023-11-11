package examples.checkers

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.foundation.Thing
import gemini.geometry.Rectangle

class Checker(
    val side: Side,
    boardPosition: IntOffset,
) : Thing() {
    private var boardPosition: IntOffset = boardPosition
        set(value) {
            field = value
            position.location.set((value.x + 1) * 10f, (value.y + 1) * 10f)
        }

    var isJumped: Boolean = false
    var isKing: Boolean = false

    private var dragging: Offset? = null
    private val rectangle = Rectangle()

    init {
        this.boardPosition = boardPosition
    }

    fun drag(offset: Offset) {
        dragging = offset
        println("$offset")
        Stage.instance?.step()
    }

    override fun DrawScope.draw() {
        if (isJumped) return
        val grid = size.minDimension / 9
        val radius = size.minDimension / 22
        val center = dragging ?: Offset((boardPosition.x + 1) * grid, (boardPosition.y + 1) * grid)
        drawCircle(side.color, radius, center)
        if (isKing) {
            drawCircle(Color.Gray, radius * .75f, center)
        }
        rectangle.set(center - Offset(radius, radius) / 2f, Size(radius, radius) * 2f)
    }

    fun drop(position: IntOffset) {
        boardPosition = position
        dragging = null
    }

    fun isHit(offset: Offset): Boolean {
        return rectangle.contains(offset).also {
            if (it) {
                dragging = offset
            }
        }
    }

    fun king() {
        isKing = true
        Stage.instance?.step()
    }
}

fun SceneScope.piece(side: Side, boardPosition: IntOffset): Checker {
    return Checker(side, boardPosition).also {
        add(it)
    }
}

enum class Side(val color: Color) {
    White(Color.White),
    Black(Color.Black)
}
