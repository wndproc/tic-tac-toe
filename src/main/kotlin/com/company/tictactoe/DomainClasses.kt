package com.company.tictactoe

import java.time.LocalDateTime

const val FIELD_SIZE = 10

class NotFoundException(message: String?) : RuntimeException(message)

class User(val id: Int, val sessionId: String, val name: String)

class Field(
        val id: Int,
        val name: String,
        val owner: User,
        val players: MutableSet<User> = HashSet(),
        val cells: Array<Cell?> = Array(FIELD_SIZE * FIELD_SIZE) { null },
        var lastMoveTime: LocalDateTime? = null
) {
    init {
        players.add(owner)
    }
}

class Cell(val type: CellType, val user: User)

enum class CellType {
    X, O
}

class FieldTo(
        val id: Int?,
        val name: String?,
        val ownerId: Int?,
        val playersNumber: Int?,
        val lastMoveTime: LocalDateTime?
) {
    constructor(field: Field) : this(
            field.id,
            field.name,
            field.owner.id,
            field.players.size,
            field.lastMoveTime
    )

    constructor(id : Int, playersNumber: Int) : this(id, null, null, playersNumber, null)
    constructor(id : Int, lastMoveTime: LocalDateTime) : this(id, null, null, null, lastMoveTime)
}
