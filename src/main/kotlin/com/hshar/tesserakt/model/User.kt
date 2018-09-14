package com.hshar.tesserakt.model

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

// TODO: Remove password from serialized object.
@Document(collection="users")
data class User (
        var id: String,
        var name: String,
        var username: String,
        var email: String,
        var organizationName: String,
        var password: String,
        @DBRef
        var roles: Set<Role>
)
