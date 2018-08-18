package com.company.tictactoe.service

import com.company.tictactoe.domain.NotFoundException
import com.company.tictactoe.domain.User
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class UserService {
    private val users = HashMap<String, User>()
    private val idCounter = AtomicInteger(1)

    fun createUser(sessionId: String, name: String) : User {
        var user = User(idCounter.getAndIncrement(), sessionId, name)
        users[sessionId] = user
        return user
    }

    fun getUser(sessionId: String): User {
        return users[sessionId] ?: throw NotFoundException("User not found, sessionId: $sessionId")
    }
}