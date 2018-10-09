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
import java.util.*
import javax.validation.Valid
import com.hshar.tesserakt.Exception.AppException
import com.hshar.tesserakt.type.RoleName
import com.hshar.tesserakt.model.Role
import com.hshar.tesserakt.model.SignUpToken
import com.hshar.tesserakt.repository.RoleRepository
import com.hshar.tesserakt.repository.SignUpTokenRepository
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder


@RestController
@RequestMapping("/api/auth")
class AuthenticationController {
    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

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

    // TODO: Make this accessable to admin user only!
    @GetMapping("/signupToken")
    fun generateSignUpToken(@RequestParam(value = "email")  email: String): SignUpToken {
        return signUpTokenRepository.insert(
            SignUpToken(email, UUID.randomUUID().toString().replace("-", ""), Date())
        )
    }

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<ApiResponse> {

        if (!signUpTokenRepository.existsByEmailAndToken(signUpRequest.email, signUpRequest.signUpToken)) {
            return ResponseEntity(
                ApiResponse(false, "Token invalid or expired!"),
                HttpStatus.BAD_REQUEST
            )
        }

        if (userRepository.existsByUsername(signUpRequest.username)) {
            return ResponseEntity(
                    ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST
            )
        }

        if (userRepository.existsByEmail(signUpRequest.email)) {
            return ResponseEntity(
                    ApiResponse(false, "Email is already in use!"),
                    HttpStatus.BAD_REQUEST
            )
        }

        val userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow{AppException("User Role not set.")}

        val password = passwordEncoder.encode(signUpRequest.password)

        val user = User(
                UUID.randomUUID().toString(),
                signUpRequest.name,
                signUpRequest.username,
                signUpRequest.email,
                signUpRequest.organizationName,
                password,
                Collections.singleton(userRole)
        )

        val result = userRepository.save(user)

        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.username).toUri()
        return ResponseEntity.created(location).body(ApiResponse(true, "User registered successfully!"))
    }

    // TODO: Remove this, this is temporary to create roles.
    @PostMapping("/roles")
    fun createRole(): ResponseEntity<ApiResponse> {
        roleRepository.insert(Role(UUID.randomUUID().toString(), RoleName.ROLE_USER))
        roleRepository.insert(Role(UUID.randomUUID().toString(), RoleName.ROLE_ADMIN))
        return ResponseEntity.ok(ApiResponse(true, "Two roles created!"))
    }
}
