package com.company.tictactoe

import java.time.LocalDateTime

const val FIELD_SIZE = 10

class NotFoundException(message: String?) : RuntimeException(message)
class User(val sessionId: String, val name: String)
class Field(
        val id: Int,
        val name: String,
        val owner: User,
        val players: MutableSet<User> = HashSet(),
        val cells: Array<Cell?> = Array(FIELD_SIZE * FIELD_SIZE) { null },
        var lastMoveDateTime: LocalDateTime? = null
) {
    init {
        players.add(owner)
    }
}

class Cell(val type: CellType, val user: User)
enum class CellType {
    X, O
}
