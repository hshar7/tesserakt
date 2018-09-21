package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.MatchingCriteria
import com.hshar.tesserakt.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface MatchingCriteriaRepository : MongoRepository<MatchingCriteria, String> {
    fun findOneById(id: String): MatchingCriteria
    fun findByUser(user: User): List<MatchingCriteria>
}
