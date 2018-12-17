package com.hshar.tesserakt.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "syndicates")
data class Syndicate (
    val id: String,
    var name: String,
    var members: MutableList<SyndicateMember>
)
