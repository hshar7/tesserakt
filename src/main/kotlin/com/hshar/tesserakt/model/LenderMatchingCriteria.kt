package com.hshar.tesserakt.model

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection="lender_matching_criteria")
data class LenderMatchingCriteria(
        val id: String,
        var jurisdiction: String?,
        var capitalAmount: Int,
        var interestRate: Float,
        var loanType: String?,
        var maturity: Int?,
        var assetClass: String?,
        var assetRating: List<String>,
        var createdAt: Date,
        var updatedAt: Date
)
