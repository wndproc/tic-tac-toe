package com.company.tictactoe.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FieldTest {

    @Test(expected = IllegalArgumentException::class)
    fun cellIdIsLessThenZero() {
        val field = createField()
        field.addMove(-1, CellType.X)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cellIdIsLargerThenFieldSize() {
        val field = createField()
        field.addMove(FIELD_SIZE * FIELD_SIZE, CellType.X)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cellIsNotEmpty() {
        val field = createField()
        field.addMove(0, CellType.X)
        field.addMove(0, CellType.X)
    }

    @Test
    fun winRow() {
        val field = createField()
        assertThat(field.addMove(0, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(1, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(2, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(3, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(4, CellType.X)).isEqualTo(Result.WIN)
    }

    @Test
    fun winColumn() {
        val field = createField()
        assertThat(field.addMove(0, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(10, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(20, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(30, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(40, CellType.X)).isEqualTo(Result.WIN)
    }

    @Test
    fun winDiagonal() {
        val field = createField()
        assertThat(field.addMove(0, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(11, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(22, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(33, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(44, CellType.X)).isEqualTo(Result.WIN)
    }

    @Test
    fun winAntidiagonal() {
        val field = createField()
        assertThat(field.addMove(9, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(18, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(27, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(36, CellType.X)).isEqualTo(Result.NOTHING)
        assertThat(field.addMove(45, CellType.X)).isEqualTo(Result.WIN)
    }

    @Test
    fun draw() {
        val field = createField()
        var cross = true
        for (row in 0 until FIELD_SIZE) {
            assertThat(field.addMove(getCellId(row, 0), getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(field.addMove(getCellId(row, 1), getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(field.addMove(getCellId(row, 2), getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(field.addMove(getCellId(row, 3), getCellType(cross))).isEqualTo(Result.NOTHING)
            cross = !cross
            assertThat(field.addMove(getCellId(row, 4), getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(field.addMove(getCellId(row, 5), getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(field.addMove(getCellId(row, 6), getCellType(cross))).isEqualTo(Result.NOTHING)
            assertThat(field.addMove(getCellId(row, 7), getCellType(cross))).isEqualTo(Result.NOTHING)
            cross = !cross
            assertThat(field.addMove(getCellId(row, 8), getCellType(cross))).isEqualTo(Result.NOTHING)
            if (row != FIELD_SIZE - 1)
                assertThat(field.addMove(getCellId(row, 9), getCellType(cross))).isEqualTo(Result.NOTHING)
            else
                assertThat(field.addMove(getCellId(row, 9), getCellType(cross))).isEqualTo(Result.DRAW)
            cross = !cross
        }
    }

    private fun getCellId(row: Int, col: Int) = row * FIELD_SIZE + col
    private fun getCellType(cross: Boolean) = if (cross) CellType.X else CellType.O

    private fun createField(): Field {
        return Field(1, "fieldName", User(1, "sessionId", "userName"))
    }
}