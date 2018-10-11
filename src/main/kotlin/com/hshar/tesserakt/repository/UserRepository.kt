package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findByEmail(email: String): User
    fun findByUsernameOrEmail(username: String, email: String): User
    fun findByUsername(username: String): User
    fun findOneById(id: String): User
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}
