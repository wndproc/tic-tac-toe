package com.company.tictactoe.domain

const val FIELD_SIZE = 10
const val WIN_NUMBER = 5

data class Player(val id: Int, val sessionId: String, val name: String)

enum class Side {
    X, O
}

enum class Result {
    NOTHING, WIN, DRAW
}