package com.company.tictactoe.service

import com.company.tictactoe.domain.*
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class FieldService {
    private val fields = LinkedHashMap<Int, Field>()
    private val idCounter = AtomicInteger(1)

    fun createField(fieldName: String, user: User): Field {
        var field = Field(idCounter.getAndIncrement(), fieldName, user)
        fields[field.id] = field
        return field
    }

    fun getFields(): List<Field> {
        return fields.values.toList()
    }

    fun getField(fieldId : Int): Field {
        return fields[fieldId] ?: throw NotFoundException("Field not found, fieldId: $fieldId")
    }

    fun join(id: Int, user: User): Boolean {
        val field = fields[id]
        if (field != null && !field.players.contains(user)) {
            field.players.add(user)
            return true
        }
        return false
    }

    fun addMove(fieldId: Int, cellId: Int, cellType: CellType): Result {
        var field: Field = fields[fieldId] ?: throw NotFoundException("Field not found, fieldId: $fieldId")
        return field.addMove(cellId, cellType)
    }
}