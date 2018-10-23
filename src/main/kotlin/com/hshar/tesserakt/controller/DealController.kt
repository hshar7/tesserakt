package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hshar.tesserakt.model.*
import com.hshar.tesserakt.repository.DealRepository
import com.hshar.tesserakt.repository.NotificationRepository
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
import kotlin.NoSuchElementException

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

    @Autowired
    lateinit var notificationRepository: NotificationRepository

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
    fun editDeal(@PathVariable dealId: String, @RequestBody body: String, @CurrentUser currentUser: UserPrincipal): ResponseEntity<String> {
        val dealJson = Gson().fromJson<JsonObject>(body)

        val deal = dealRepository.findOneById(dealId)

        if (deal.underwriter.id != currentUser.id) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        deal.borrowerName = dealJson["borrowerName"].asString
        deal.borrowerDescription = dealJson["borrowerDescription"].asString
        deal.jurisdiction = Jurisdiction.valueOf(dealJson["jurisdiction"].asString)
        deal.capitalAmount = dealJson["capitalAmount"].asFloat
        deal.interestRate = dealJson["interestRate"].asFloat
        deal.loanType = LoanType.valueOf(dealJson["loanType"].asString)
        deal.maturity = dealJson["maturity"].asInt

        // TODO: Make sure each syndicate member's ready is invalidated.

        val updatedDeal = dealRepository.save(deal)

        return ResponseEntity(Gson().toJson(updatedDeal), HttpStatus.OK)
    }

    @DeleteMapping("/deal/{dealId}")
    @PreAuthorize("hasRole('USER')")
    fun deleteDeal(@PathVariable dealId: String, @CurrentUser currentUser: UserPrincipal): ResponseEntity<String> {
        val deal = dealRepository.findOneById(dealId)
        if (deal.underwriter.id != currentUser.id)
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        dealRepository.delete(deal)
        return ResponseEntity("{\"status\": \"success\"}", HttpStatus.OK)
    }

    @PutMapping("/deal/{dealId}/subscribe")
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

            try { // try to just update contribution if the member already in syndicate
                val member = syndicate.members.first { it.user.id == user.id }
                member.contribution = subscriptionDetails["subscriptionAmount"].asFloat
                syndicate.members.removeAll { it.user.id == user.id }
                syndicate.members.add(member)

            } catch (e: NoSuchElementException) {
                syndicate.members.add(
                    SyndicateMember(
                        UUID.randomUUID().toString(),
                        user,
                        subscriptionDetails["subscriptionAmount"].asFloat
                    )
                )
            }

            syndicate.members.forEach { it.ready = false }
            syndicateRepository.save(syndicate)

            var totalSubscription = 0.0.toFloat()
            syndicate.members.forEach { (_, _, contribution) -> totalSubscription += contribution }
            deal.subscription = totalSubscription

            if (totalSubscription >= deal.capitalAmount) {
                deal.status = Status.OPEN
            } else if (totalSubscription < deal.capitalAmount) {
                deal.status = Status.NEW // Reopens the deal if the new subscription amount is too low.
            }

            dealRepository.save(deal)
            return ResponseEntity(Gson().toJson(deal), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @DeleteMapping("/deal/{dealId}/subscribe")
    @PreAuthorize("hasRole('USER')")
    fun unsubscribeToDeal(@PathVariable dealId: String, @CurrentUser user: UserPrincipal): ResponseEntity<String> {

        val deal = dealRepository.findOneById(dealId)
        // Update syndicate
        val syndicate = deal.syndicate
        syndicate.members.removeAll { it.user.id == user.id }

        syndicate.members.forEach { it.ready = false }

        syndicateRepository.save(syndicate)

        var totalSubscription = 0.0.toFloat()
        syndicate.members.forEach { (_, _, contribution) -> totalSubscription += contribution }
        deal.subscription = totalSubscription

        if (totalSubscription >= deal.capitalAmount) {
            deal.status = Status.OPEN
        } else if (totalSubscription < deal.capitalAmount) {
            deal.status = Status.NEW // Reopens the deal if the new subscription amount is too low.
        }
        dealRepository.save(deal)

        return ResponseEntity("{\"status\": \"success\"}", HttpStatus.OK)
    }

    @PutMapping("/deal/{dealId}/readyUp")
    @PreAuthorize("hasRole('USER')")
    fun subscriberReady(@PathVariable dealId: String, @CurrentUser user: UserPrincipal): ResponseEntity<String> {

        val deal = dealRepository.findOneById(dealId)
        val syndicate = deal.syndicate
        val member = syndicate.members.first{ it.user.id == user.id }
        member.ready = true
        syndicate.members.removeAll{ it.user.id == user.id }
        syndicate.members.add(member)
        syndicateRepository.save(syndicate)

        var allReady = true
        syndicate.members.forEach{ if (it.ready == false) allReady = false }
        if (allReady)
            deal.status = Status.IN_PROGRESS

        dealRepository.save(deal)

        return ResponseEntity(Gson().toJson(user), HttpStatus.OK)
    }

    @GetMapping("/deals")
    @PreAuthorize("hasRole('USER')")
    fun getAllDeals(): List<Deal> {
        return dealRepository.findAll()
    }

    @PostMapping("/deal/{dealId}/invite")
    @PreAuthorize("hasRole('USER')")
    fun inviteToDeal(@PathVariable dealId: String, @PathParam("email") email: String): ResponseEntity<String> {

        val user = userRepository.findByEmail(email)

        notificationRepository.insert(Notification(
            UUID.randomUUID().toString(),
            user,
            "You've been invited to participate in a new deal.",
            "/market/$dealId",
            Date()
        ))

        return ResponseEntity("{\"status\": \"success\"}", HttpStatus.OK)
    }
}
