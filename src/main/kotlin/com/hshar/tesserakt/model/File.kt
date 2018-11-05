package com.hshar.tesserakt.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "files")
data class File (
    @Id
    var fileName: String,
    val owner: User,
    val deal: Deal
)
