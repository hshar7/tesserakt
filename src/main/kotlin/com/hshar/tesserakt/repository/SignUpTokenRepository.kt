package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.SignUpToken
import org.springframework.data.mongodb.repository.MongoRepository

interface SignUpTokenRepository : MongoRepository<SignUpToken, String> {
    fun existsByEmailAndToken(email: String, token: String): Boolean
    fun findByEmailAndToken(email: String, token: String): SignUpToken
}
