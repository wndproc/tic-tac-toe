package com.company.tictactoe.service

import com.company.tictactoe.domain.Field
import com.company.tictactoe.domain.Player
import com.company.tictactoe.domain.Result
import com.company.tictactoe.domain.Side
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class FieldService {
    private val fields = ConcurrentHashMap<Int, Field>()
    private val idCounter = AtomicInteger(1)

    fun createField(fieldName: String, player: Player): Field {
        val field = Field(idCounter.getAndIncrement(), fieldName, player)
        fields[field.id] = field
        return field
    }

    fun getFields(): List<Field> {
        return fields.values.toList().sortedBy { it.id }
    }

    fun getField(fieldId: Int): Field? {
        return fields[fieldId]
    }

    fun deleteField(fieldId: Int) {
        fields.remove(fieldId)
    }

    fun addPlayer(id: Int, player: Player): Boolean {
        val field = fields[id]
        if (field != null && !field.players.contains(player)) {
            field.players.add(player)
            return true
        }
        return false
    }

    fun addMove(fieldId: Int, cellId: Int, side: Side, player: Player): Result {
        val field: Field = fields[fieldId] ?: throw RuntimeException("Field not found, fieldId: $fieldId")
        return field.addMove(cellId, side, player)
    }
}