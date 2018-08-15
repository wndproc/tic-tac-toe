package com.company.tictactoe.controller

import com.company.tictactoe.FieldTo
import com.company.tictactoe.User
import com.company.tictactoe.service.FieldService
import com.company.tictactoe.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SendToUser
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

    @MessageMapping("/users/create")
    @SendToUser("/queue/user")
    fun createUser(userRequest: UserTo, @Header("simpSessionId") sessionId: String): UserTo {
        return UserTo(userService.createUser(sessionId, userRequest.name))
    }

    @SubscribeMapping("/fields")
    fun getFields(): List<FieldTo> {
        return fieldService.getFields().map(::FieldTo).toList()
    }

    @MessageMapping("/fields/create")
    @SendTo("/topic/fields")
    fun createField(field: FieldTo, @Header("simpSessionId") sessionId: String): FieldTo {
        return FieldTo(fieldService.createField(field.name!!, userService.getUser(sessionId)))
    }

    @MessageMapping("/fields/{fieldId}/join")
    @SendTo("/topic/fields")
    fun joinField(@DestinationVariable fieldId: Int, @Header("simpSessionId") sessionId: String): FieldTo? {
        return if (fieldService.join(fieldId, userService.getUser(sessionId)))
            FieldTo(fieldId, fieldService.getField(fieldId).players.size)
        else null
    }

    class UserTo(val id: Int?, val name: String) {
        constructor(user: User) : this(user.id, user.name)
    }
}