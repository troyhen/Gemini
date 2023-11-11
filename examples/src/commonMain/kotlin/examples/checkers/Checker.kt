package examples.checkers

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import examples.checkers.Side.Bottom
import examples.checkers.Side.Top
import gemini.engine.SceneScope
import gemini.engine.Stage
import gemini.foundation.Thing
import gemini.geometry.Rectangle

class Checker(
    val side: Side,
    boardPosition: IntOffset,
) : Thing() {
    var boardPosition: IntOffset = boardPosition
        private set(value) {
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

    fun moves(): List<Move> {
        val nw = Move(this, IntOffset(boardPosition.x - 1, boardPosition.y - 1))
        val ne = Move(this, IntOffset(boardPosition.x + 1, boardPosition.y - 1))
        val sw = Move(this, IntOffset(boardPosition.x - 1, boardPosition.y + 1))
        val se = Move(this, IntOffset(boardPosition.x + 1, boardPosition.y + 1))
        val nwj = Move(this, IntOffset(boardPosition.x - 2, boardPosition.y - 2), IntOffset(boardPosition.x - 1, boardPosition.y - 1))
        val nej = Move(this, IntOffset(boardPosition.x + 2, boardPosition.y - 2), IntOffset(boardPosition.x + 1, boardPosition.y - 1))
        val swj = Move(this, IntOffset(boardPosition.x - 2, boardPosition.y + 2), IntOffset(boardPosition.x - 1, boardPosition.y + 1))
        val sej = Move(this, IntOffset(boardPosition.x + 2, boardPosition.y + 2), IntOffset(boardPosition.x + 1, boardPosition.y + 1))
        val all = if (isKing) {
            listOf(nw, ne, sw, se, nwj, nej, swj, nej)
        } else when (side) {
            Top -> listOf(sw, se, swj, sej)
            Bottom -> listOf(nw, ne, nwj, nej)
        }
        return all.filter { it.newPosition.x in 0..7 && it.newPosition.y in 0..7 }
    }
}

fun SceneScope.piece(side: Side, boardPosition: IntOffset): Checker {
    return Checker(side, boardPosition).also {
        add(it)
    }
}

data class Move(val checker: Checker, val newPosition: IntOffset, val jumpPosition: IntOffset = IntOffset(-1, -1)) {
    val isNotJump: Boolean get() = jumpPosition.x < 0
    val isJump: Boolean get() = !isNotJump
}

enum class Side(val color: Color) {
    Top(Color.White),
    Bottom(Color.Black)
}
