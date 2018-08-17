package com.hshar.tesserakt.model

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection="loans")
data class Loan(
        var uuid: String,
        var jurisdiction: String?,
        var capitalAmount: Int,
        var interestRate: Float,
        var loanType: String?,
        var maturity: Int?,
        var assetClass: String?,
        var assetRating: String?,
        var issuingPartyRiskProfile: Int?,
        var status: String,
        var createdAt: Date,
        var updatedAt: Date
)
