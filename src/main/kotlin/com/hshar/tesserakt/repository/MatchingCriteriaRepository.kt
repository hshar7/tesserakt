package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.MatchingCriteria
import org.springframework.data.mongodb.repository.MongoRepository

interface MatchingCriteriaRepository : MongoRepository<MatchingCriteria, String> {
    fun findOneById(id: String): MatchingCriteria
}
