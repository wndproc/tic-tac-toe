package com.company.tictactoe.dto

import com.company.tictactoe.domain.CellType
import com.company.tictactoe.domain.Field
import com.company.tictactoe.domain.Result
import com.company.tictactoe.domain.User
import java.time.LocalDateTime

class UserTo(val id: Int?, val name: String) {
    constructor(user: User) : this(user.id, user.name)
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

    constructor(id: Int, playersNumber: Int) : this(id, null, null, playersNumber, null)
    constructor(id: Int, lastMoveTime: LocalDateTime) : this(id, null, null, null, lastMoveTime)
}

class MoveTo(val cellId: Int, val cellType: CellType, var result : Result?, var user: UserTo?)
