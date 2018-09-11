package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hshar.tesserakt.model.Deal
import com.hshar.tesserakt.model.Syndicate
import com.hshar.tesserakt.repository.DealRepository
import com.hshar.tesserakt.repository.Deals
import com.hshar.tesserakt.repository.SyndicateRepository
import com.hshar.tesserakt.security.CurrentUser
import com.hshar.tesserakt.security.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import javax.websocket.server.PathParam
import kotlin.NoSuchElementException

@RestController
@RequestMapping("/api")
class DealController {
    @Autowired
    lateinit var dealRepository: DealRepository

    @Autowired
    lateinit var syndicateRepository: SyndicateRepository

    @Autowired
    lateinit var deals: Deals

    @GetMapping("/deal/{id}")
    @PreAuthorize("hasRole('USER')")
    fun getDeal(@PathVariable id: String): String {
        return deals.findOneById(id).toString()
    }

    @GetMapping("/deals-by-status")
    @PreAuthorize("hasRole('USER')")
    fun getDealsByStatus(@PathParam(value = "status") status: String): String {
        return deals.getDealsByStatus(status).toString()
    }

    @PostMapping("/deal")
    @PreAuthorize("hasRole('USER')")
    fun createDeal(@RequestBody body: String, @CurrentUser currentUser: UserPrincipal): Deal {
        val deal = Gson().fromJson<JsonObject>(body)

        deal["id"] = UUID.randomUUID().toString()
        deal["underwriterId"] = currentUser.id
        deal["status"] = "New"
        deal["assetRating"] = "Not Rated" // TODO: Remove this after Rating Agency is implemented
        deal["assetClass"] = "Not Rated" // TODO: Remove this after Rating Agency is implemented
        deal["subscription"] = 0
        deal["syndicateId"] = UUID.randomUUID().toString()
        deal["createdAt"] = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        deal["updatedAt"] = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

        syndicateRepository.insert(Syndicate(
            deal["syndicateId"].asString,
            deal["syndicateName"].asString,
            mutableMapOf(Pair(currentUser.id, 0.toFloat()))
        ))

        // TODO: Run Async task to check this deal against all lender matching criteria. Kafka?

        return dealRepository.insert(Gson().fromJson<Deal>(deal))
    }

    @PutMapping("/deal/{dealId}")
    @PreAuthorize("hasRole('USER')")
    fun subscribeToDeal(@PathVariable dealId: String, @RequestBody body: String): ResponseEntity<String> {
        val subscriptionDetails = Gson().fromJson<JsonObject>(body)

        if (subscriptionDetails["userId"].asString.isNotEmpty() &&
            subscriptionDetails["subscriptionAmount"].isJsonPrimitive) {
            val deal = dealRepository.findOneById(dealId)

            // Update syndicate
            val syndicate = syndicateRepository.findOneById(deal.syndicateId)
                    .orElseThrow{ NoSuchElementException("Syndicate cannot be found ${deal.syndicateId}") }
            syndicate.members[subscriptionDetails["userId"].asString] =
                    subscriptionDetails["subscriptionAmount"].asFloat
            syndicateRepository.save(syndicate)


            deal.subscription = deal.subscription + subscriptionDetails["subscriptionAmount"].asInt
            dealRepository.save(deal)
            // TODO: Check if goal amount of deal is reached, modify deal to be of Open status

            return ResponseEntity(HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/deals")
    @PreAuthorize("hasRole('USER')")
    fun getAllDeals(): List<Deal> {
        return dealRepository.findAll()
    }
}
