package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.Syndicate
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface SyndicateRepository : MongoRepository<Syndicate, String> {
    fun findOneById(id: String): Optional<Syndicate>
}
