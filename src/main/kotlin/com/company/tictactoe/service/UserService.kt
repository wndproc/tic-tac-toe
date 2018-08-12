package com.company.tictactoe.service

import com.company.tictactoe.NotFoundException
import com.company.tictactoe.User
import org.springframework.stereotype.Service

@Service
class UserService {
    private val users = HashMap<String, User>()

    fun createUser(sessionId: String, name: String) {
        users[sessionId] = User(sessionId, name)
    }

    fun getUser(sessionId: String): User {
        return users[sessionId] ?: throw NotFoundException("User not found, sessionId: $sessionId")
    }
}