package com.company.tictactoe.dto

import com.company.tictactoe.domain.*
import java.time.LocalDateTime

class PlayerTo(val id: Int? = null, val name: String) {
    constructor(player: Player) : this(player.id, player.name)
}

class FieldInfoTo(
        val id: Int?,
        val name: String? = null,
        val creatorId: Int? = null,
        val playersNumber: Int? = null,
        val lastMoveTime: LocalDateTime? = null
) {
    constructor(field: Field) : this(
            field.id,
            field.name,
            field.creator.id,
            field.players.size,
            field.lastMoveTime
    )
}

class FieldCellsTo(
        val id: Int,
        val name: String,
        val cells: Array<Array<Side?>> = Array(FIELD_SIZE) { Array<Side?>(FIELD_SIZE) { null } },
        var lastPlayerId: Int? = null
) {
    constructor(field: Field) : this(
            field.id,
            field.name,
            field.cells,
            field.lastPlayerId
    )
}

class MoveTo(val cellId: Int, val side: Side, var result: Result?, var player: PlayerTo?)

class Message<T>(val payload: T? = null)
