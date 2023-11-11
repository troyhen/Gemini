package examples.checkers

import androidx.compose.ui.unit.IntOffset

data class Move(val checker: Checker, val newPosition: IntOffset, val jumpPosition: IntOffset = IntOffset(-1, -1))
