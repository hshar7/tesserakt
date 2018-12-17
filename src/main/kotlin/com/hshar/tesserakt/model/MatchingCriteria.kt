package com.hshar.tesserakt.model

import com.hshar.tesserakt.type.AssetClass
import com.hshar.tesserakt.type.AssetRating
import com.hshar.tesserakt.type.Jurisdiction
import com.hshar.tesserakt.type.LoanType
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "matching_criteria")
data class MatchingCriteria(
    val id: String,
    @DBRef
    val user: User,
    var jurisdiction: MutableList<Jurisdiction>,
    var capitalAmountMin: Float,
    var capitalAmountMax: Float,
    var interestRateMin: Float,
    var interestRateMax: Float,
    var loanType: MutableList<LoanType>,
    var maturityMin: Int,
    var maturityMax: Int,
    var assetClass: MutableList<AssetClass>,
    var assetRating: MutableList<AssetRating>,
    var createdAt: Date,
    var updatedAt: Date
)
