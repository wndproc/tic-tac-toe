package com.company.tictactoe.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class FieldTest {
    private val firstPlayer: Player = Player(1, "sessionId_1", "userName_1")
    private val secondPlayer: Player = Player(2, "sessionId_2", "userName_2")
    private var field: Field = createField()
    private var firstPlayerTurn: Boolean = false

    @Before
    fun setUp() {
        field = createField()
    }

    @Test(expected = IllegalArgumentException::class)
    fun cellIdIsLessThenZero() {
        field.addMove(-1, Side.X, firstPlayer)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cellIdIsLargerThenFieldSize() {
        field.addMove(FIELD_SIZE * FIELD_SIZE, Side.X, firstPlayer)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cellIsNotEmpty() {
        addMove(0, 0, Side.X)
        addMove(0, 0, Side.X)
    }

    @Test(expected = IllegalArgumentException::class)
    fun userIsNotInPlayers() {
        field.addMove(0, Side.X, Player(3, "sessionId_3", "userName_3"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun multipleMovesFromOneUser() {
        field.addMove(0, Side.X, firstPlayer)
        field.addMove(1, Side.X, firstPlayer)
    }

    @Test
    fun winRow() {
        assertThat(addMove(0, 0, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(0, 1, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(0, 2, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(0, 3, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(0, 4, Side.X)).isEqualTo(Result.WIN)
    }

    @Test
    fun winColumn() {
        assertThat(addMove(0, 0, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(1, 0, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(2, 0, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(3, 0, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(4, 0, Side.X)).isEqualTo(Result.WIN)
    }

    @Test
    fun winDiagonal() {
        assertThat(addMove(0, 0, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(1, 1, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(2, 2, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(3, 3, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(4, 4, Side.X)).isEqualTo(Result.WIN)
    }

    @Test
    fun winAntidiagonal() {
        assertThat(addMove(0,9, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(1,8, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(2,7, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(3,6, Side.X)).isEqualTo(Result.NOTHING)
        assertThat(addMove(4,5, Side.X)).isEqualTo(Result.WIN)
    }

    @Test
    fun winMultipleMoves() {
           assertThat(addMove(2, 4, Side.O)).isEqualTo(Result.NOTHING)
           assertThat(addMove(3, 4, Side.X)).isEqualTo(Result.NOTHING)
           assertThat(addMove(4, 4, Side.O)).isEqualTo(Result.NOTHING)
           assertThat(addMove(4, 3, Side.X)).isEqualTo(Result.NOTHING)
           assertThat(addMove(4, 5, Side.O)).isEqualTo(Result.NOTHING)
           assertThat(addMove(3, 3, Side.X)).isEqualTo(Result.NOTHING)
           assertThat(addMove(3, 5, Side.O)).isEqualTo(Result.NOTHING)
           assertThat(addMove(2, 2, Side.X)).isEqualTo(Result.NOTHING)
           assertThat(addMove(4, 2, Side.O)).isEqualTo(Result.NOTHING)
           assertThat(addMove(2, 3, Side.X)).isEqualTo(Result.NOTHING)
           assertThat(addMove(1, 3, Side.X)).isEqualTo(Result.NOTHING)
           assertThat(addMove(5, 3, Side.O)).isEqualTo(Result.NOTHING)
           assertThat(addMove(0, 3, Side.X)).isEqualTo(Result.WIN)
    }

    @Test
    fun draw() {
        var cross = true
        for (row in 0 until FIELD_SIZE) {
            assertThat(addMove(row, 0, getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(addMove(row, 1, getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(addMove(row, 2, getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(addMove(row, 3, getCellType(cross))).isEqualTo(Result.NOTHING)
            cross = !cross
            assertThat(addMove(row, 4, getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(addMove(row, 5, getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(addMove(row, 6, getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(addMove(row, 7, getCellType(cross))).isEqualTo(Result.NOTHING)
            cross = !cross
            assertThat(addMove(row, 8, getCellType(cross))).isEqualTo(Result.NOTHING)
            if (row != FIELD_SIZE - 1)
                assertThat(addMove(row, 9, getCellType(cross))).isEqualTo(Result.NOTHING)
            else
                assertThat(addMove(row, 9, getCellType(cross))).isEqualTo(Result.DRAW)
            cross = !cross
        }
    }

    private fun createField(): Field {
        val field = Field(1, "fieldName", firstPlayer)
        field.players.add(secondPlayer)
        return field
    }

    private fun addMove(row: Int, col: Int, side: Side) : Result {
        firstPlayerTurn = !firstPlayerTurn
        return field.addMove(getCellId(row, col), side, if (firstPlayerTurn) firstPlayer else secondPlayer)
    }

    private fun getCellId(row: Int, col: Int) = row * FIELD_SIZE + col
    private fun getCellType(cross: Boolean) = if (cross) Side.X else Side.O
}