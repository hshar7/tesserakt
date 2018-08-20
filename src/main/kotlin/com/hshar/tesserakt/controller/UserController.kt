package com.hshar.tesserakt.controller

import com.hshar.tesserakt.Exception.ResourceNotFoundException
import com.hshar.tesserakt.payload.UserIdentityAvailability
import com.hshar.tesserakt.payload.UserProfile
import com.hshar.tesserakt.payload.UserSummary
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.security.CurrentUser
import com.hshar.tesserakt.security.UserPrincipal
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class UserController {
    @Autowired
    lateinit var userRepository: UserRepository

    companion object: KLogging()

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUser(@CurrentUser currentUser: UserPrincipal): UserSummary {
        return UserSummary(currentUser.id, currentUser.username, currentUser.name)
    }

    @GetMapping("/user/checkUsernameAvailability")
    fun checkUsernameAvailability(@RequestParam(value = "username") username: String): UserIdentityAvailability {
        return UserIdentityAvailability(!userRepository.existsByUsername(username))
    }

    @GetMapping("/user/checkEmailAvailability")
    fun checkEmailAvailability(@RequestParam(value = "email") email: String): UserIdentityAvailability {
        return UserIdentityAvailability(!userRepository.existsByEmail(email))
    }

    @GetMapping("/users/{username}")
    fun getUserProfile(@PathVariable(value = "username") username: String): UserProfile {
        val user = userRepository.findByUsername(username)
                .orElseThrow{ResourceNotFoundException("User", "username", username)}

        return UserProfile(user.id, user.username, user.name)
    }
}
