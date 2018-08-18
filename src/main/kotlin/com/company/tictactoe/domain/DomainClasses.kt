package com.company.tictactoe.domain

import java.time.LocalDateTime

const val FIELD_SIZE = 10
const val WIN_NUMBER = 5

class NotFoundException(message: String?) : RuntimeException(message)

class User(val id: Int, val sessionId: String, val name: String)

enum class CellType {
    X, O
}

enum class Result {
    NOTHING, WIN, DRAW
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
