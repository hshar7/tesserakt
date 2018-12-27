package com.hshar.tesserakt.model

import com.hshar.tesserakt.type.Jurisdiction
import com.hshar.tesserakt.type.LoanType
import com.hshar.tesserakt.type.AssetRating
import com.hshar.tesserakt.type.AssetClass
import com.hshar.tesserakt.type.Status
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "deals")
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
