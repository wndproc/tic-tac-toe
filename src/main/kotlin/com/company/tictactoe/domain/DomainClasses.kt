package com.company.tictactoe.domain

const val FIELD_SIZE = 10
const val WIN_NUMBER = 5

class NotFoundException(message: String?) : RuntimeException(message)

data class User(val id: Int, val sessionId: String, val name: String)

enum class Side {
    X, O
}

enum class Result {
    NOTHING, WIN, DRAW
}