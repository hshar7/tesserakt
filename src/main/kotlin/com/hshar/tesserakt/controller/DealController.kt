package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hshar.tesserakt.model.Deal
import com.hshar.tesserakt.model.Syndicate
import com.hshar.tesserakt.model.SyndicateMember
import com.hshar.tesserakt.repository.DealRepository
import com.hshar.tesserakt.repository.SyndicateRepository
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.security.CurrentUser
import com.hshar.tesserakt.security.UserPrincipal
import com.hshar.tesserakt.type.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.websocket.server.PathParam
import org.springframework.kafka.core.KafkaTemplate

@RestController
@RequestMapping("/api")
class DealController {
    @Autowired
    lateinit var dealRepository: DealRepository

    @Autowired
    lateinit var syndicateRepository: SyndicateRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @GetMapping("/deal/{id}")
    @PreAuthorize("hasRole('USER')")
    fun getDeal(@PathVariable id: String): Deal {
        return dealRepository.findOneById(id)
    }

    @GetMapping("/deals-by-status")
    @PreAuthorize("hasRole('USER')")
    fun getDealsByStatus(@PathParam(value = "status") status: String): List<Deal> {
        return dealRepository.findByStatus(Status.valueOf(status))
    }

    @GetMapping("/my-open-deals")
    @PreAuthorize("hasRole('USER')")
    fun getOpenDealsByUserId(@CurrentUser currentUser: UserPrincipal): List<Deal> {
        val user = userRepository.findByUsername(currentUser.username)
            .orElseThrow{ UsernameNotFoundException("{${currentUser.username} not found.")}

        val deals = mutableListOf<Deal>()
        dealRepository.findByStatusIn(listOf(Status.NEW, Status.OPEN)).forEach{
            if (it.syndicate.members.filter { it.user.id == user.id }.isNotEmpty()) {
                deals.add(it)
            }
        }

        return deals
    }

    @PostMapping("/deal")
    @PreAuthorize("hasRole('USER')")
    fun createDeal(@RequestBody body: String, @CurrentUser currentUser: UserPrincipal): Deal {
        val deal = Gson().fromJson<JsonObject>(body)

        val user = userRepository.findByUsername(currentUser.username)
            .orElseThrow { UsernameNotFoundException("Username ${currentUser.username} not found.") }

        deal["assetRating"] = "NotRated" // TODO: Remove this after Rating Agency is implemented
        deal["assetClass"] = "NotRated" // TODO: Remove this after Rating Agency is implemented

        val syndicate = syndicateRepository.insert(Syndicate(
            UUID.randomUUID().toString(),
            deal["syndicateName"].asString,
            mutableListOf(SyndicateMember(UUID.randomUUID().toString(), user, deal["underwriterAmount"].asFloat))
        ))

        val theRealDeal = Deal(
            UUID.randomUUID().toString(),
            user,
            deal["borrowerName"].asString,
            deal["borrowerDescription"].asString,
            deal["underwriterAmount"].asFloat,
            Jurisdiction.valueOf(deal["jurisdiction"].asString),
            deal["capitalAmount"].asFloat,
            deal["interestRate"].asFloat,
            LoanType.valueOf(deal["loanType"].asString),
            deal["maturity"].asInt,
            AssetClass.valueOf(deal["assetClass"].asString),
            AssetRating.valueOf(deal["assetRating"].asString),
            syndicate,
            Status.NEW,
            Date(),
            Date()
        )

        kafkaTemplate.send("streaming.deals.newDeals", Gson().toJson(theRealDeal))

        return dealRepository.insert(theRealDeal)
    }

    @PutMapping("/deal/{dealId}")
    @PreAuthorize("hasRole('USER')")
    fun subscribeToDeal(@PathVariable dealId: String, @RequestBody body: String): ResponseEntity<String> {
        val subscriptionDetails = Gson().fromJson<JsonObject>(body)

        if (subscriptionDetails["userId"].asString.isNotEmpty() &&
            subscriptionDetails["subscriptionAmount"].isJsonPrimitive) {
            val deal = dealRepository.findOneById(dealId)

            // Update syndicate
            val syndicate = deal.syndicate

            val user = userRepository.findById(subscriptionDetails["userId"].asString)
                .orElseThrow { UsernameNotFoundException("${subscriptionDetails["userId"].asString} not found.") }

            syndicate.members.add(
                SyndicateMember(UUID.randomUUID().toString(), user, subscriptionDetails["subscriptionAmount"].asFloat))
            syndicateRepository.save(syndicate)

            var totalSubscription = 0.0.toFloat()
            syndicate.members.forEach { (_, _, contribution) -> totalSubscription += contribution }
            deal.subscription = totalSubscription

            if (deal.subscription >= deal.capitalAmount) {
                deal.status = Status.OPEN
            }

            dealRepository.save(deal)

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
