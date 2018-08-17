package com.hshar.tesserakt.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="roles")
data class Role (
    var id: String,
    var name: RoleName
)
