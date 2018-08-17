package com.hshar.tesserakt.payload

import javax.validation.constraints.NotBlank

data class LoginRequest (
    @NotBlank
    var usernameOrEmail: String,

    @NotBlank
    var password: String
)
