package com.hshar.tesserakt.model

import com.hshar.tesserakt.type.RoleName
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="roles")
data class Role (
    var id: String,
    var name: RoleName
)
