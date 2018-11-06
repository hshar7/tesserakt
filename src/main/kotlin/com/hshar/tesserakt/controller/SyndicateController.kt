package com.hshar.tesserakt.controller

import com.hshar.tesserakt.model.Syndicate
import com.hshar.tesserakt.model.User
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.security.CurrentUser
import com.hshar.tesserakt.security.UserPrincipal
import com.hshar.tesserakt.service.SyndicateDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SyndicateController {
    @Autowired
    lateinit var syndicateDAO: SyndicateDAO

    @Autowired
    lateinit var userRepository: UserRepository

    @GetMapping("/syndicate")
    @PreAuthorize("hasRole('LENDER','UNDERWRITER')")
    fun getMySyndicates(@CurrentUser userDetails: UserPrincipal): List<Syndicate> {
        val user = userRepository.findByUsername(userDetails.username)

        return syndicateDAO.findByMembership(user)
    }

    @GetMapping("/syndicateRecommendation")
    @PreAuthorize("hasRole('LENDER','UNDERWRITER')")
    fun getSyndicateRecommendation(@CurrentUser userDetails: UserPrincipal): List<User> {
        val user = userRepository.findByUsername(userDetails.username)

        return syndicateDAO.findPastSyndicateMembers(user)
    }
}
