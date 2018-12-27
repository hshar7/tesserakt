package com.hshar.tesserakt.controller

import com.hshar.tesserakt.exception.ResourceNotFoundException
import com.hshar.tesserakt.model.Role
import com.hshar.tesserakt.model.SignUpToken
import com.hshar.tesserakt.model.User
import com.hshar.tesserakt.payload.UserIdentityAvailability
import com.hshar.tesserakt.payload.UserProfile
import com.hshar.tesserakt.payload.UserSummary
import com.hshar.tesserakt.repository.RoleRepository
import com.hshar.tesserakt.repository.SignUpTokenRepository
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.security.CurrentUser
import com.hshar.tesserakt.security.UserPrincipal
import com.hshar.tesserakt.type.RoleName
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.util.UUID
import java.util.Date

@RestController
@RequestMapping("/api")
class UserController {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var signUpTokenRepository: SignUpTokenRepository

    companion object: KLogging()

    @GetMapping("/user/me")
    fun getCurrentUser(@CurrentUser currentUser: UserPrincipal): UserSummary {
        return UserSummary(
            currentUser.id,
            currentUser.username,
            currentUser.name,
            currentUser.email,
            currentUser.organizationName,
            currentUser.authorities
        )
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    fun getCurrentUser(): List<User> {
        return userRepository.findAll()
    }

    @GetMapping("/user/checkUsernameAvailability")
    fun checkUsernameAvailability(@RequestParam(value = "username") username: String): UserIdentityAvailability {
        return UserIdentityAvailability(!userRepository.existsByUsername(username))
    }

    @GetMapping("/user/checkEmailAvailability")
    fun checkEmailAvailability(@RequestParam(value = "email") email: String): UserIdentityAvailability {
        return UserIdentityAvailability(!userRepository.existsByEmail(email))
    }

    @GetMapping("/users/{id}")
    fun getUserProfileById(@PathVariable(value = "id") id: String): UserProfile {
        val user = userRepository.findById(id)
                .orElseThrow{ResourceNotFoundException("User", "id", id)}

        return UserProfile(user.id, user.username, user.name, user.organizationName, user.email)
    }

    @PostMapping("/user/signupToken")
    @PreAuthorize("hasRole('ADMIN')")
    fun generateSignUpToken(
        @RequestParam(value = "email")  email: String,
        @RequestParam(value = "roles")  roles: List<String>
    ): SignUpToken {

        val roleSet = mutableSetOf<Role>()
        roles.forEach {
            val role = roleRepository.findByName(RoleName.valueOf(it))
                .orElseThrow { ResourceNotFoundException("Role not found", "name", it) }
            roleSet.add(role)
        }

        return signUpTokenRepository.insert(
            SignUpToken(email, UUID.randomUUID().toString().replace("-", ""), roleSet, Date())
        )
    }
}
