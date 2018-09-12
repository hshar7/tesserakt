package com.hshar.tesserakt.model

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
    var jurisdiction: String,
    var capitalAmount: Float,
    var interestRate: Float,
    var loanType: String,
    var maturity: Int,
    var assetClass: String,
    var assetRating: String,
    @DBRef
    var syndicate: Syndicate,
    var status: String,
    var createdAt: Date,
    var updatedAt: Date
)
