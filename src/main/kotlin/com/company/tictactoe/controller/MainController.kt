package com.company.tictactoe.controller

import com.company.tictactoe.Field
import com.company.tictactoe.service.FieldService
import com.company.tictactoe.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller

@Controller
class MainController {
    val fieldService: FieldService
    val userService: UserService

    @Autowired
    constructor(fieldService: FieldService, userService: UserService) {
        this.fieldService = fieldService
        this.userService = userService
    }

    @MessageMapping("/login")
    fun login(user: UserTo, @Header("simpSessionId") sessionId: String) {
        userService.createUser(sessionId, user.name)
    }

    @SubscribeMapping("/fields")
    fun getFields(): List<Field> {
        return fieldService.getFields()
    }

    @MessageMapping("/fields/create")
    @SendTo("/topic/fields")
    fun createField(field: FieldTo, @Header("simpSessionId") sessionId: String): Field {
        return fieldService.createField(field.name, userService.getUser(sessionId))
    }

    @MessageMapping("/fields/{fieldId}/join/")
    @SendTo("/topic/fields")
    fun joinField(@DestinationVariable fieldId: Int, @Header("simpSessionId") sessionId: String): Field? {
        return fieldService.join(fieldId, userService.getUser(sessionId))
    }

    class UserTo(val name: String)
    class FieldTo(val name: String)
}