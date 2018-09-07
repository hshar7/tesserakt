package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.Deal
import org.springframework.data.mongodb.repository.MongoRepository

interface DealRepository : MongoRepository<Deal, String> {
    fun findOneByUuid(id: String): Deal

    fun findByStatus(status: String): List<Deal>
}
