package com.company.tictactoe.controller

import com.company.tictactoe.domain.Result.NOTHING
import com.company.tictactoe.dto.*
import com.company.tictactoe.service.FieldService
import com.company.tictactoe.service.PlayersService
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
    val playersService: PlayersService
    val messagingTemplate: SimpMessagingTemplate

    @Autowired
    constructor(fieldService: FieldService, playersService: PlayersService, messagingTemplate: SimpMessagingTemplate) {
        this.fieldService = fieldService
        this.playersService = playersService
        this.messagingTemplate = messagingTemplate
    }

    @SubscribeMapping("/fields")
    fun getFields(): Message<List<FieldInfoTo>> {
        return Message(fieldService.getFields().map(::FieldInfoTo).toList())
    }

    @MessageMapping("/fields/create")
    @SendTo("/topic/fields")
    fun createField(field: FieldInfoTo, @Header("simpSessionId") sessionId: String): Message<FieldInfoTo> {
        return Message(FieldInfoTo(fieldService.createField(field.name!!, playersService.findPlayer(sessionId)!!)))
    }

    @MessageMapping("/fields/{fieldId}/join")
    @SendTo("/topic/fields")
    fun joinField(@DestinationVariable fieldId: Int, @Header("simpSessionId") sessionId: String): Message<FieldInfoTo> {
        return if (fieldService.join(fieldId, playersService.findPlayer(sessionId)!!))
            Message(FieldInfoTo(fieldId, playersNumber = fieldService.getField(fieldId)!!.players.size))
        else Message()
    }

    @SubscribeMapping("/fields/{fieldId}")
    fun getField(@DestinationVariable fieldId: Int): Message<FieldCellsTo> {
        var field = fieldService.getField(fieldId)
        return if (field != null) Message(FieldCellsTo(field)) else Message()
    }

    @MessageMapping("/fields/{fieldId}/move")
    @SendTo("/topic/field/{fieldId}/move")
    fun addMove(move: MoveTo, @DestinationVariable fieldId: Int, @Header("simpSessionId") sessionId: String): Message<MoveTo> {
        var player = playersService.findPlayer(sessionId)!!
        var result = fieldService.addMove(fieldId, move.cellId, move.side, player)
        messagingTemplate.convertAndSend("/topic/fields", Message(FieldInfoTo(fieldId, lastMoveTime = LocalDateTime.now())))
        if (result != NOTHING) {
            deleteField(fieldId)
        }
        return Message(MoveTo(move.cellId, move.side, result, PlayerTo(player)))
    }

    private fun deleteField(fieldId: Int) {
        fieldService.deleteField(fieldId)
        messagingTemplate.convertAndSend("/topic/fields/delete", Message(FieldInfoTo(fieldId)))
    }
}