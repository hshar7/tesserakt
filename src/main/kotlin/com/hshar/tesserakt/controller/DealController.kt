package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hshar.tesserakt.model.Deal
import com.hshar.tesserakt.model.Syndicate
import com.hshar.tesserakt.model.SyndicateMember
import com.hshar.tesserakt.model.Notification
import com.hshar.tesserakt.repository.DealRepository
import com.hshar.tesserakt.repository.NotificationRepository
import com.hshar.tesserakt.repository.SyndicateRepository
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.security.CurrentUser
import com.hshar.tesserakt.security.UserPrincipal
import com.hshar.tesserakt.service.Web3jService
import com.hshar.tesserakt.type.AssetClass
import com.hshar.tesserakt.type.AssetRating
import com.hshar.tesserakt.type.Jurisdiction
import com.hshar.tesserakt.type.LoanType
import com.hshar.tesserakt.type.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.userdetails.UsernameNotFoundException
import javax.websocket.server.PathParam
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.DeleteMapping
import java.util.UUID
import java.util.Date
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

    @Autowired
    lateinit var web3jService: Web3jService

    @GetMapping("/deal/{id}")
    @PreAuthorize("hasAnyRole('UNDERWRITER','LENDER', 'ADMIN')")
    fun getDeal(@PathVariable id: String): Deal {
        return dealRepository.findOneById(id)
    }

    @GetMapping("/deals-by-status")
    @PreAuthorize("hasAnyRole('UNDERWRITER','LENDER', 'ADMIN', 'RATING_AGENCY')")
    fun getDealsByStatus(@PathParam(value = "status") status: String): List<Deal> {
        return dealRepository.findByStatus(Status.valueOf(status))
    }

    @GetMapping("/my-open-deals")
    @PreAuthorize("hasAnyRole('UNDERWRITER','LENDER')")
    fun getOpenDealsByUserId(@CurrentUser currentUser: UserPrincipal): List<Deal> {
        val user = userRepository.findByUsername(currentUser.username)

        val deals = mutableListOf<Deal>()
        dealRepository.findByStatusIn(listOf(Status.NEW, Status.OPEN)).forEach {
            if (it.syndicate.members.filter { it.user.id == user.id }.isNotEmpty()) {
                deals.add(it)
            }
        }

        return deals
    }

    @PostMapping("/deal")
    @PreAuthorize("hasRole('UNDERWRITER')")
    fun createDeal(@RequestBody body: String, @CurrentUser currentUser: UserPrincipal): Deal {
        val deal = Gson().fromJson<JsonObject>(body)

        val user = userRepository.findByUsername(currentUser.username)

        /* TODO: Remove this after Rating Agency is implemented */
        deal["assetRating"] = "NotRated"
        deal["assetClass"] = "NotRated"

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

        web3jService.loadDealLedgerContract().addDeal(
                theRealDeal.id,
                user.id,
                Web3jService.CONTRACT_ADDRESS,
                theRealDeal.borrowerName,
                theRealDeal.jurisdiction.toString(),
                theRealDeal.capitalAmount.toString(),
                theRealDeal.interestRate.toString(),
                theRealDeal.loanType.toString(),
                theRealDeal.maturity.toBigInteger(),
                theRealDeal.assetClass.toString(),
                theRealDeal.assetRating.toString(),
                Gson().toJson(theRealDeal.syndicate),
                theRealDeal.status.toString()
        ).sendAsync()

        kafkaTemplate.send("streaming.deals.newDeals", Gson().toJson(theRealDeal))

        return dealRepository.insert(theRealDeal)
    }

    @PutMapping("/deal/{dealId}")
    @PreAuthorize("hasRole('UNDERWRITER')")
    fun editDeal(@PathVariable dealId: String, @RequestBody body: String, @CurrentUser currentUser: UserPrincipal):
            ResponseEntity<String> {
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

        val syndicate = deal.syndicate
        syndicate.members.forEach { it.ready = false }
        syndicateRepository.save(syndicate)

        val updatedDeal = dealRepository.save(deal)

        web3jService.loadDealLedgerContract().updateDeal(
            deal.id,
            deal.underwriter.id,
            Web3jService.CONTRACT_ADDRESS,
            deal.borrowerName,
            deal.jurisdiction.toString(),
            deal.capitalAmount.toString(),
            deal.interestRate.toString(),
            deal.loanType.toString(),
            deal.maturity.toBigInteger(),
            deal.assetClass.toString(),
            deal.assetRating.toString(),
            Gson().toJson(deal.syndicate),
            deal.status.toString())
        .sendAsync()

        return ResponseEntity(Gson().toJson(updatedDeal), HttpStatus.OK)
    }

    @DeleteMapping("/deal/{dealId}")
    @PreAuthorize("hasRole('UNDERWRITER')")
    fun deleteDeal(@PathVariable dealId: String, @CurrentUser currentUser: UserPrincipal): ResponseEntity<String> {
        val deal = dealRepository.findOneById(dealId)
        if (deal.underwriter.id != currentUser.id)
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        dealRepository.delete(deal)
        return ResponseEntity("{\"status\": \"success\"}", HttpStatus.OK)
    }

    @PutMapping("/deal/{dealId}/subscribe")
    @PreAuthorize("hasRole('LENDER')")
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
    @PreAuthorize("hasRole('LENDER')")
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
    @PreAuthorize("hasAnyRole('UNDERWRITER','LENDER')")
    fun subscriberReady(@PathVariable dealId: String, @CurrentUser user: UserPrincipal): ResponseEntity<String> {

        val deal = dealRepository.findOneById(dealId)
        val syndicate = deal.syndicate
        val member = syndicate.members.first { it.user.id == user.id }
        member.ready = true
        syndicate.members.removeAll { it.user.id == user.id }
        syndicate.members.add(member)
        syndicateRepository.save(syndicate)

        var allReady = true
        syndicate.members.forEach { if (!it.ready) allReady = false }
        if (allReady) {
            deal.status = Status.IN_PROGRESS

            syndicate.members.forEach {
                notificationRepository.insert(Notification(
                        UUID.randomUUID().toString(),
                        it.user,
                        "All members ready! Deal for ${deal.borrowerName} is live!",
                        "/dashboard",
                        Date()
                ))
            }
        }

        dealRepository.save(deal)

        return ResponseEntity(Gson().toJson(user), HttpStatus.OK)
    }

    @GetMapping("/deals")
    @PreAuthorize("hasAnyRole('UNDERWRITER','LENDER', 'ADMIN')")
    fun getAllDeals(): List<Deal> {
        return dealRepository.findAll()
    }

    @PostMapping("/deal/{dealId}/invite")
    @PreAuthorize("hasAnyRole('UNDERWRITER','LENDER')")
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
