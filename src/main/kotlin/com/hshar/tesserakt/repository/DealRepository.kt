package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.Deal
import org.springframework.data.mongodb.repository.MongoRepository

interface DealRepository : MongoRepository<Deal, String> {
    fun findOneById(id: String): Deal

    fun findByStatus(status: String): List<Deal>

    fun findByStatusIn(statuses: Collection<String>): List<Deal>
}
