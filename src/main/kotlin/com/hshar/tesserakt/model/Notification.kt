package com.hshar.tesserakt.model

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "notifications")
data class Notification(
    val id: String,
    @DBRef
    val user: User,
    val message: String,
    val url: String,
    val createdAt: Date
)
