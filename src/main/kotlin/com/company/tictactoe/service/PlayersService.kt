package com.company.tictactoe.service

import com.company.tictactoe.domain.Player
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class PlayersService {
    private val players = ConcurrentHashMap<String, Player>()
    private val idCounter = AtomicInteger(1)

    fun createPlayer(sessionId: String, name: String): Player {
        val player = Player(idCounter.getAndIncrement(), sessionId, name)
        if (players.putIfAbsent(sessionId, player) != null) {
            throw IllegalArgumentException("Player already created, sessionId $sessionId")
        }
        return player
    }

    fun findPlayer(sessionId: String): Player? {
        return players[sessionId]
    }
}