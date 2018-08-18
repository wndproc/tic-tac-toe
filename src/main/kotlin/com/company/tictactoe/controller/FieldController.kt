package com.company.tictactoe.controller

import com.company.tictactoe.domain.CellType
import com.company.tictactoe.domain.Field
import com.company.tictactoe.domain.FieldTo
import com.company.tictactoe.domain.Result
import com.company.tictactoe.service.FieldService
import com.company.tictactoe.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.time.LocalDateTime


@Controller
class FieldController {
    val fieldService: FieldService
    val userService: UserService
    val messagingTemplate: SimpMessagingTemplate

    @Autowired
    constructor(fieldService: FieldService, userService: UserService, messagingTemplate: SimpMessagingTemplate) {
        this.fieldService = fieldService
        this.userService = userService
        this.messagingTemplate = messagingTemplate
    }

    @SubscribeMapping("/fields/{fieldId}")
    fun getField(@DestinationVariable fieldId: Int): Field {
        return fieldService.getField(fieldId)
    }

    @MessageMapping("/fields/{fieldId}/move")
    @SendTo("/topic/field/{fieldId}/move")
    fun addMove(move: MoveTo, @DestinationVariable fieldId: Int, @Header("simpSessionId") sessionId: String): MoveTo? {
        var user = userService.getUser(sessionId)
        if (user != null) {
            move.result = fieldService.addMove(fieldId, move.cellId, move.type)
            messagingTemplate.convertAndSend("/topic/fields", FieldTo(fieldId, LocalDateTime.now()))
            return move
        }
        return null
    }

    class MoveTo(val cellId: Int, val type: CellType, var result : Result?)
}