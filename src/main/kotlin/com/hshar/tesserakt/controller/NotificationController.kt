package com.hshar.tesserakt.controller

import com.hshar.tesserakt.model.Notification
import com.hshar.tesserakt.repository.NotificationRepository
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.security.CurrentUser
import com.hshar.tesserakt.security.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class NotificationController {
    @Autowired
    lateinit var notificationRepository: NotificationRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @GetMapping("/notification/have")
    @PreAuthorize("hasRole('USER')")
    fun getIHaveNotifications(@CurrentUser userDetails: UserPrincipal): Boolean {

        val user = userRepository.findByUsername(userDetails.username)

        return notificationRepository.existsByUser(user)
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasRole('USER')")
    fun getMyNotifications(@CurrentUser userDetails: UserPrincipal): List<Notification> {
        val user = userRepository.findByUsername(userDetails.username)

        return notificationRepository.findByUser(user)
    }
}
