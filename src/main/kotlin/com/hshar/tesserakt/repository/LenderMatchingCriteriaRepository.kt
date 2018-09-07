package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.LenderMatchingCriteria
import org.springframework.data.mongodb.repository.MongoRepository

interface LenderMatchingCriteriaRepository : MongoRepository<LenderMatchingCriteria, String> {
    fun findOneById(id: String): LenderMatchingCriteria
}
