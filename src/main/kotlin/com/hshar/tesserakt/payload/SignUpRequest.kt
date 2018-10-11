package com.hshar.tesserakt.payload

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class SignUpRequest(
    @NotBlank
    @get:Size(min = 4, max = 40)
    var name: String,

    @NotBlank
    @get:Size(min = 3, max = 15)
    var username: String,

    @NotBlank
    @get:Size(max = 40)
    @get:Email
    var email: String,

    @NotBlank
    @get:Size(min = 6, max = 20)
    var password: String,

    @NotBlank
    @get:Size(min = 3, max = 20)
    var organizationName: String,

    @NotBlank
    @get:Size(min = 32, max = 32)
    var signUpToken: String
)
