package com.hshar.tesserakt.model

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection="deals")
data class Deal(
        val uuid: String,
        val underwriterId: String,
        var jurisdiction: String?,
        var capitalAmount: Int,
        var interestRate: Float,
        var loanType: String?,
        var maturity: Int,
        var assetClass: String?,
        var assetRating: String?,
        var syndicateId: String,
        var status: String,
        var createdAt: Date,
        var updatedAt: Date
)
