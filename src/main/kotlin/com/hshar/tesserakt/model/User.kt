package com.hshar.tesserakt.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="users")
data class User (
        var id: String,
        var name: String,
        var username: String,
        var email: String,
        var organizationName: String,
        var password: String,
        var roles: Set<Role>
)
