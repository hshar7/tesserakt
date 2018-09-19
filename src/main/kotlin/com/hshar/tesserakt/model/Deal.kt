package com.hshar.tesserakt.model

import com.hshar.tesserakt.type.*
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection="deals")
data class Deal(
    val id: String,
    @DBRef
    val underwriter: User,
    var borrowerName: String,
    var borrowerDescription: String,
    var subscription: Float,
    var jurisdiction: Jurisdiction,
    var capitalAmount: Float,
    var interestRate: Float,
    var loanType: LoanType,
    var maturity: Int,
    var assetClass: AssetClass,
    var assetRating: AssetRating,
    @DBRef
    var syndicate: Syndicate,
    var status: Status,
    var createdAt: Date,
    var updatedAt: Date
)
