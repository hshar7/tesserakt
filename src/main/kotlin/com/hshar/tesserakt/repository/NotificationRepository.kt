package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.Notification
import com.hshar.tesserakt.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface NotificationRepository : MongoRepository<Notification, String> {
    fun findByUser(user: User): List<Notification>
    fun existsByUser(user: User): Boolean
}
