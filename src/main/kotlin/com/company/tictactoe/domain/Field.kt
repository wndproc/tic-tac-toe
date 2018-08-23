package com.company.tictactoe.domain

import java.lang.Math.max
import java.lang.Math.min
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class Field(
        val id: Int,
        val name: String,
        val creator: Player,
        val players: MutableSet<Player> = ConcurrentHashMap.newKeySet(),
        val cells: Array<Array<Side?>> = Array(FIELD_SIZE) { Array<Side?>(FIELD_SIZE) { null } },
        var lastMoveTime: LocalDateTime? = null,
        var lastPlayerId: Int? = null,
        private var movesCount: Int = 0
) {
    init {
        players.add(creator)
    }

    @Synchronized
    fun addMove(cellId: Int, side: Side, player: Player): Result {
        if (cellId !in 0..(FIELD_SIZE * FIELD_SIZE - 1)) {
            throw IllegalArgumentException("Wrong cellId: $cellId")
        }
        if (!players.contains(player)) {
            throw IllegalArgumentException("Player is not joined to the field, fieldId: $id, playerId: ${player.id}")
        }
        if (player.id == lastPlayerId) {
            throw IllegalArgumentException("Player is trying to make multiple moves, playerId: ${player.id}")
        }
        val row = cellId / FIELD_SIZE
        val col = cellId % FIELD_SIZE
        if (cells[row][col] != null) {
            throw IllegalArgumentException("Cell is not empty, cellId: $cellId")
        }
        cells[row][col] = side
        lastMoveTime = LocalDateTime.now()
        lastPlayerId = player.id
        movesCount++

        if (
                //check row
                checkWin(row, max(col - WIN_NUMBER + 1, 0), row, min(col + WIN_NUMBER - 1, FIELD_SIZE - 1), 0, 1, side) ||
                //check col
                checkWin(max(row - WIN_NUMBER + 1, 0), col, min(row + WIN_NUMBER - 1, FIELD_SIZE - 1), col, 1, 0, side) ||
                //check diagonal
                checkWin(
                        max(row - WIN_NUMBER + 1, max(row - col, 0)),
                        max(col - WIN_NUMBER + 1, max(col - row, 0)),
                        min(row + WIN_NUMBER - 1, min(FIELD_SIZE - 1 + row - col, FIELD_SIZE - 1)),
                        min(col + WIN_NUMBER - 1, min(FIELD_SIZE - 1 + col - row, FIELD_SIZE - 1)),
                        1,
                        1,
                        side
                ) ||
                //check anti-diagonal
                checkWin(
                        max(row - WIN_NUMBER + 1, max(row - col, 0)),
                        min(col + WIN_NUMBER - 1, min(FIELD_SIZE - 1 + col - row, FIELD_SIZE - 1)),
                        min(row + WIN_NUMBER - 1, min(FIELD_SIZE - 1 + row - col, FIELD_SIZE - 1)),
                        max(col - WIN_NUMBER + 1, max(col - row, 0)),
                        1,
                        -1,
                        side
                )
        ) return Result.WIN

        if (movesCount == FIELD_SIZE * FIELD_SIZE) {
            return Result.DRAW
        }

        return Result.NOTHING
    }

    private fun checkWin(rowFrom: Int, colFrom: Int, rowTo: Int, colTo: Int, rowInc: Int, colInc: Int, side: Side): Boolean {
        var row = rowFrom
        var col = colFrom
        var counter = 0
        while (row != rowTo + rowInc || col != colTo + colInc) {
            if (cells[row][col] == side) {
                counter++
                if (counter == WIN_NUMBER) {
                    return true
                }
            } else {
                counter = 0
            }
            row += rowInc
            col += colInc
        }
        return false
    }
}