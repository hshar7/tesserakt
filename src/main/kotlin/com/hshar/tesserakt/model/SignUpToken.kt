package com.hshar.tesserakt.model

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "signup-tokens")
data class SignUpToken (
    val email: String,
    val token: String,
    val roles: Set<Role>,
    @Indexed(expireAfterSeconds = 604800)
    val createdAt: Date
)
