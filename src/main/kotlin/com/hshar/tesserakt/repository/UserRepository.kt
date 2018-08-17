package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface UserRepository : MongoRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun findByUsernameOrEmail(username: String, email: String): Optional<User>
    fun findByIdIn(userIds: List<Long>): List<User>
    fun findByUsername(username: String): Optional<User>
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}
