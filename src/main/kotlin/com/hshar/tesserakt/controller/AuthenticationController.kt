package com.hshar.tesserakt.controller

import com.hshar.tesserakt.model.User
import com.hshar.tesserakt.payload.ApiResponse
import com.hshar.tesserakt.payload.JwtAuthenticationResponse
import com.hshar.tesserakt.payload.LoginRequest
import com.hshar.tesserakt.payload.SignUpRequest
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.validation.Valid
import com.hshar.tesserakt.repository.SignUpTokenRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/api/auth")
class AuthenticationController {
    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    lateinit var signUpTokenRepository: SignUpTokenRepository

    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<JwtAuthenticationResponse> {
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.usernameOrEmail, loginRequest.password
                )
        )
        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtTokenProvider.generateToken(authentication)
        return ResponseEntity.ok(JwtAuthenticationResponse(jwt))
    }

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<ApiResponse> {

        var badRequest = false
        var badRequestResponse = ""
        if (!signUpTokenRepository.existsByEmailAndToken(signUpRequest.email, signUpRequest.signUpToken)) {
            badRequest = true
            badRequestResponse = "Token invalid or expired!"
        }

        if (userRepository.existsByUsername(signUpRequest.username)) {
            badRequest = true
            badRequestResponse = "Username is already taken!"
        }

        if (userRepository.existsByEmail(signUpRequest.email)) {
            badRequest = true
            badRequestResponse = "Email is already in use!"
        }
        if (badRequest) {
            return ResponseEntity(
                    ApiResponse(false, badRequestResponse),
                    HttpStatus.BAD_REQUEST
            )
        }

        val userRoles = signUpTokenRepository.findByEmailAndToken(signUpRequest.email, signUpRequest.signUpToken).roles
        val password = passwordEncoder.encode(signUpRequest.password)
        val user = User(
                UUID.randomUUID().toString(),
                signUpRequest.name,
                signUpRequest.username,
                signUpRequest.email,
                signUpRequest.organizationName,
                password,
                userRoles
        )

        val result = userRepository.save(user)
        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.username).toUri()
        return ResponseEntity.created(location).body(ApiResponse(true, "User registered successfully!"))
    }
}
