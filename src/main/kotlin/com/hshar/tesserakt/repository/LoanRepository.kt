package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.Loan
import org.springframework.data.mongodb.repository.MongoRepository

interface LoanRepository : MongoRepository<Loan, String> {
    fun findOneByUuid(id: String): Loan
}
