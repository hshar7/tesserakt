package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hshar.tesserakt.model.LenderMatchingCriteria
import com.hshar.tesserakt.repository.LenderMatchingCriteriaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

@RestController
@RequestMapping("/api")
class MatchingCriteriaController {
    @Autowired
    lateinit var lenderMatchingCriteriaRepository: LenderMatchingCriteriaRepository

    @GetMapping("/matchingCriteria/{id}")
    @PreAuthorize("hasRole('USER')")
    fun getMatchingCriteria(@PathVariable id: String): LenderMatchingCriteria {
        return lenderMatchingCriteriaRepository.findOneById(id)
    }

    @PostMapping("/matchingCriteria")
    @PreAuthorize("hasRole('USER')")
    fun createMatchingCriteria(@RequestBody body: String): LenderMatchingCriteria {
        val matchingCriteria = Gson().fromJson<JsonObject>(body)

        matchingCriteria["id"] = UUID.randomUUID().toString()
        matchingCriteria["createdAt"] = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        matchingCriteria["updatedAt"] = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

        return lenderMatchingCriteriaRepository.insert(Gson().fromJson<LenderMatchingCriteria>(matchingCriteria))
    }
}
