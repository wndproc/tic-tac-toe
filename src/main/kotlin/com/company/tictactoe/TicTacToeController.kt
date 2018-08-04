package com.company.tictactoe

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Controller
class TicTacToeController {
    val fieldService: FieldService
    val userService: UserService

    @Autowired
    constructor(fieldService: FieldService, userService: UserService) {
        this.fieldService = fieldService
        this.userService = userService
    }

    @MessageMapping("/login")
    fun login(user: UserName, @Header("simpSessionId") sessionId: String) {
        userService.createUser(sessionId, user.name)
    }

    @SubscribeMapping("/fields")
    fun getFields(): List<Field> {
        return fieldService.getFields()
    }

    @MessageMapping("/field/create")
    @SendTo("/topic/field")
    fun createField(fieldName: FieldName, @Header("simpSessionId") sessionId: String): Field {
        return fieldService.createField(fieldName.name, userService.getUser(sessionId)!!)
    }

    @MessageMapping("/field/join")
    @SendTo("/topic/field")
    fun joinField(fieldId: FieldId, @Header("simpSessionId") sessionId: String): Field? {
        return fieldService.join(fieldId.id, userService.getUser(sessionId)!!)
    }
}

@Service
class FieldService {
    private val fields = LinkedHashMap<Int, Field>()
    private val idCounter = AtomicInteger(1)

    fun createField(fieldName: String, user: User): Field {
        var field = Field(idCounter.getAndIncrement(), fieldName, user)
        fields[field.id] = field
        return field
    }

    fun getFields(): List<Field> {
        return fields.values.toList()
    }

    fun join(id: Int, user: User): Field? {
        val field = fields[id]
        if (field != null && !field.players.contains(user)) {
            field.players.add(user)
            return field
        }
        return null
    }
}

@Service
class UserService {
    private val users = HashMap<String, User>()

    fun createUser(id: String, name: String) {
        users[id] = User(id, name)
    }

    fun getUser(id: String): User? {
        return users[id]
    }
}

class UserName(val name: String)
class FieldName(val name: String)
class FieldId(val id: Int)
class User(val id: String, val name: String)
class Field(val id: Int, val name: String, val owner: User, val players: MutableSet<User> = HashSet()) {
    init {
        players.add(owner)
    }
}