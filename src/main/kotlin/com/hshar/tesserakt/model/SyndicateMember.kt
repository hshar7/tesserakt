package com.hshar.tesserakt.model

import org.springframework.data.mongodb.core.mapping.DBRef

data class SyndicateMember (
    val id: String,
    @DBRef
    val user: User,
    var contribution: Float,
    var ready: Boolean = false
)
