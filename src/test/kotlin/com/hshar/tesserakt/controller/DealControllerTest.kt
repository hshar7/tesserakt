package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hshar.tesserakt.TESTCONSTS
import com.hshar.tesserakt.model.Deal
import com.hshar.tesserakt.model.Syndicate
import com.hshar.tesserakt.model.SyndicateMember
import com.hshar.tesserakt.repository.DealRepository
import com.hshar.tesserakt.repository.SyndicateRepository
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.type.*
import org.junit.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DealControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var dealRepository: DealRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var syndicateRepository: SyndicateRepository

    lateinit var signInToken: String

    @Before
    fun setup() {
        val responseJson = this.mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content("{\"usernameOrEmail\": \"${TESTCONSTS.permanentTestUsername}\", \"password\": \"123123q\"}")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk).andReturn().response.contentAsString
        signInToken = Gson().fromJson<JsonObject>(responseJson)["accessToken"].asString
    }

    @Test
    fun getDealTest() {
        val dealId = UUID.randomUUID().toString()
        val (syndicate, deal) = createDeal(dealId)

        // Get Deal
        mvc.perform(get("/api/deal/$dealId").header("Authorization", "Bearer $signInToken"))
                .andExpect(status().isOk)

        // Clean up
        syndicateRepository.delete(syndicate)
        dealRepository.delete(deal)
    }

    @Test
    fun getDealByStatusTest() {
        val dealId = UUID.randomUUID().toString()
        val (syndicate, deal) = createDeal(dealId)

        // Get Deal
        val status = Status.NEW.toString()
        val newDeals = mvc.perform(
                get("/api/deals-by-status?status=$status").header("Authorization", "Bearer $signInToken")
        ).andExpect(status().isOk).andReturn().response.contentAsString

        val newDealsJsonArray = Gson().fromJson<JsonArray>(newDeals)
        var exists = false
        newDealsJsonArray.forEach {
            if (it["id"].asString == dealId) {
                exists = true
            }
        }
        Assert.assertTrue(exists)

        // Clean up
        syndicateRepository.delete(syndicate)
        dealRepository.delete(deal)
    }

    @Test
    fun getAllDealsTest() {
        val dealId = UUID.randomUUID().toString()
        val (syndicate, deal) = createDeal(dealId)

        // Get Deal
        val newDeals = mvc.perform(
                get("/api/deals").header("Authorization", "Bearer $signInToken")
        ).andExpect(status().isOk).andReturn().response.contentAsString

        val newDealsJsonArray = Gson().fromJson<JsonArray>(newDeals)
        var exists = false
        newDealsJsonArray.forEach {
            if (it["id"].asString == dealId) {
                exists = true
            }
        }
        Assert.assertTrue(exists)

        // Clean up
        syndicateRepository.delete(syndicate)
        dealRepository.delete(deal)
    }

    @Test
    fun getOpenDealsByUserIdTest() {
        val dealId = UUID.randomUUID().toString()
        val (syndicate, deal) = createDeal(dealId)

        // Get Deal
        val newDeals = mvc.perform(
                get("/api/my-open-deals").header("Authorization", "Bearer $signInToken")
        ).andExpect(status().isOk).andReturn().response.contentAsString

        val newDealsJsonArray = Gson().fromJson<JsonArray>(newDeals)
        var exists = false
        newDealsJsonArray.forEach {
            if (it["id"].asString == dealId) {
                exists = true
            }
        }
        Assert.assertTrue(exists)

        // Clean up
        syndicateRepository.delete(syndicate)
        dealRepository.delete(deal)
    }

    @Test
    fun createDealTest() {
        val borrowerName = UUID.randomUUID().toString()
        val dealJson = JsonObject()
        dealJson["borrowerName"] = borrowerName
        dealJson["borrowerDescription"] = "TestData"
        dealJson["underwriterAmount"] = 15
        dealJson["jurisdiction"] = "US"
        dealJson["capitalAmount"] = 100
        dealJson["interestRate"] = 3.8
        dealJson["loanType"] = "Term"
        dealJson["maturity"] = 60
        dealJson["syndicateName"] = "White Rose"

        // Create Deal
        mvc.perform(
                post("/api/deal").header("Authorization", "Bearer $signInToken")
                        .contentType(MediaType.APPLICATION_JSON).content(dealJson.toString())
        ).andExpect(status().isOk)

        val deal = dealRepository.findOneByBorrowerName(borrowerName)
        val syndicate = deal.syndicate
        Assert.assertNotNull(deal)
        Assert.assertNotNull(syndicate)

        // Clean up
        syndicateRepository.delete(syndicate)
        dealRepository.delete(deal)
    }

    @Test
    fun editDealTest() {
        val dealId = UUID.randomUUID().toString()
        val (syndicate, deal) = createDeal(dealId)

        val borrowerName = "Borrower02"
        deal.borrowerName = borrowerName

        // Edit Deal
        mvc.perform(
                put("/api/deal/$dealId").header("Authorization", "Bearer $signInToken")
                        .contentType(MediaType.APPLICATION_JSON).content(Gson().toJson(deal))
        ).andExpect(status().isOk)

        // Assert
        val updatedDeal = dealRepository.findOneById(dealId)
        Assert.assertEquals(borrowerName, updatedDeal.borrowerName)

        // Clean Up
        syndicateRepository.delete(syndicate)
        dealRepository.delete(updatedDeal)
    }

    @Test
    fun deleteDealTest() {
        val dealId = UUID.randomUUID().toString()
        val (syndicate, deal) = createDeal(dealId)

        mvc.perform(
                delete("/api/deal/$dealId").header("Authorization", "Bearer $signInToken")
        ).andExpect(status().isOk)

        // Assert
        var dealException = false
        var syndicateException = false
        try {
            dealRepository.findOneById(dealId)
        } catch (e: EmptyResultDataAccessException) {
            dealException = true
            try {
                syndicateRepository.findOneById(syndicate.id)
            } catch (e: EmptyResultDataAccessException) {
                syndicateException = true
            }
        }

        Assert.assertTrue(dealException)
        Assert.assertTrue(syndicateException)
    }

    @Test
    fun subscriptionTest() {
        val dealId = UUID.randomUUID().toString()
        var (syndicate, deal) = createDeal(dealId)

        val user = userRepository.findByUsernameOrEmail(TESTCONSTS.permanentTestUsername2, TESTCONSTS.permanentTestUsername2)

        // Sign in User2
        val responseJson = this.mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content("{\"usernameOrEmail\": \"${TESTCONSTS.permanentTestUsername2}\", \"password\": \"123123q\"}")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk).andReturn().response.contentAsString
        val signInToken2 = Gson().fromJson<JsonObject>(responseJson)["accessToken"].asString

        mvc.perform(
            put("/api/deal/$dealId/subscribe").header("Authorization", "Bearer $signInToken2")
                    .contentType(MediaType.APPLICATION_JSON).content("{\"userId\":\"${user.id}\", \"subscriptionAmount\":\"1000000\"}")
        ).andExpect(status().isOk)

        // Assert user in syndicate
        deal = dealRepository.findOneById(dealId)

        var subscribed = false
        deal.syndicate.members.forEach {
            if (it.user.id == user.id) {
                subscribed = true
            }
        }
        Assert.assertTrue(subscribed)

        // Unsubscribe Test
        mvc.perform(
                delete("/api/deal/$dealId/subscribe").header("Authorization", "Bearer $signInToken2")
        ).andExpect(status().isOk)

        // Assert user is not in syndicate
        deal = dealRepository.findOneById(dealId)

        subscribed = false
        deal.syndicate.members.forEach {
            if (it.user.id == user.id) {
                subscribed = true
            }
        }
        Assert.assertFalse(subscribed)

        // Clean Up
        syndicateRepository.delete(syndicate)
        dealRepository.delete(deal)
    }

    private fun createDeal(dealId: String): Pair<Syndicate, Deal> {
        val underwriter = userRepository.findByUsernameOrEmail(
                TESTCONSTS.permanentTestUsername, TESTCONSTS.permanentTestUsername
        )
        val syndicate = syndicateRepository.insert(
                Syndicate(
                        UUID.randomUUID().toString(),
                        "Syndicate",
                        mutableListOf(
                                SyndicateMember(UUID.randomUUID().toString(), underwriter, 5000000.toFloat(), false))
                )
        )
        val deal = Deal(
                dealId,
                underwriter,
                "borrower",
                "borrower desc.",
                5000000.toFloat(),
                Jurisdiction.UK,
                10000000.toFloat(),
                5.9.toFloat(),
                LoanType.Term,
                1500,
                AssetClass.NotRated,
                AssetRating.NotRated,
                syndicate,
                Status.NEW,
                Date(),
                Date()
        )

        // Persist the deal
        dealRepository.insert(deal)
        return Pair(syndicate, deal)
    }
}
