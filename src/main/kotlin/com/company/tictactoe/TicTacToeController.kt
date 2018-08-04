package com.company.tictactoe

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service

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

    @MessageMapping("/field/create")
    @SendTo("/topic/field")
    fun createField(field: Field): Field {
        fieldService.createField(field)
        return field
    }

    @SubscribeMapping("/fields")
    fun getFields(): List<Field> {
        return fieldService.getFields()
    }
}

@Service
class FieldService {
    private val fields = ArrayList<Field>()

    fun createField(field: Field) {
        fields.add(field)
    }

    fun getFields(): List<Field> {
        return fields
    }
}

class UserName(val name: String)
class Field(val name: String)