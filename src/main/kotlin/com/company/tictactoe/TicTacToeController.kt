package com.company.tictactoe

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Controller
class TicTacToeController {
    val fieldService: FieldService

    @Autowired
    constructor(fieldService: FieldService) {
        this.fieldService = fieldService
    }

    @MessageMapping("/login")
    fun login(user: UserName) {
        println(user.name)
    }

    @SubscribeMapping("/fields")
    fun getFields(): List<Field> {
        return fieldService.getFields()
    }

    @MessageMapping("/field/create")
    @SendTo("/topic/field")
    fun createField(fieldName: FieldName): Field {
        return fieldService.createField(fieldName.name)
    }

    @MessageMapping("/field/join")
    @SendTo("/topic/field")
    fun joinField(fieldId: FieldId): Field? {
        return fieldService.join(fieldId.id)
    }
}

@Service
class FieldService {
    private val fields = LinkedHashMap<Int, Field>()
    private val idCounter = AtomicInteger(1)

    fun createField(fieldName: String): Field {
        var field = Field(idCounter.getAndIncrement(), fieldName)
        fields.put(field.id, field)
        return field
    }

    fun getFields(): List<Field> {
        return fields.values.toList()
    }

    fun join(id: Int): Field? {
        var field = fields.get(id)
        field?.players?.incrementAndGet()
        return field
    }
}

class UserName(val name: String)
class FieldName(val name: String)
class FieldId(val id: Int)
class Field(val id: Int, val name: String, var players: AtomicInteger = AtomicInteger(1))