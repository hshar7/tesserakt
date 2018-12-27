package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.Deal
import com.hshar.tesserakt.type.Status
import org.springframework.data.mongodb.repository.MongoRepository

interface DealRepository : MongoRepository<Deal, String> {
    fun findOneById(id: String): Deal

    fun findByStatus(status: Status): List<Deal>

    fun findByStatusIn(statuses: Collection<Status>): List<Deal>

    fun findOneByBorrowerName(borrowerName: String): Deal
}
