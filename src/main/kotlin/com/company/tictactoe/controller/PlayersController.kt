package com.company.tictactoe.controller

import com.company.tictactoe.dto.Message
import com.company.tictactoe.dto.PlayerTo
import com.company.tictactoe.service.PlayersService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller

@Controller
class PlayersController {
    private val playersService: PlayersService

    @Autowired
    constructor(playersService: PlayersService) {
        this.playersService = playersService
    }

    @MessageMapping("/players/create")
    @SendToUser("/queue/player")
    fun createPlayer(player: PlayerTo, @Header("simpSessionId") sessionId: String): Message<PlayerTo> {
        return Message(PlayerTo(playersService.createPlayer(sessionId, player.name!!)))
    }

    @SubscribeMapping("/players/{sessionId}")
    fun getPlayer(@DestinationVariable sessionId: String): Message<PlayerTo> {
        var player = playersService.findPlayer(sessionId)
        return if (player != null) Message(PlayerTo(player)) else Message()
    }
}