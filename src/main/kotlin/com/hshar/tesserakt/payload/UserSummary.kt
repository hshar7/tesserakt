package com.hshar.tesserakt.payload

import org.springframework.security.core.GrantedAuthority

data class UserSummary (
    var id: String,
    var username: String,
    var name: String,
    var email: String,
    var organizationName: String,
    var authorities: Collection<GrantedAuthority>
)
