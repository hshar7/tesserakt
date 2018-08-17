package com.hshar.tesserakt.payload

data class JwtAuthenticationResponse (
        var accessToken: String,
        var tokenType: String = "Bearer"
)
