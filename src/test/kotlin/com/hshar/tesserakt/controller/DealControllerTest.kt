package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hshar.tesserakt.Exception.ResourceNotFoundException
import com.hshar.tesserakt.TESTCONSTS
import com.hshar.tesserakt.model.Deal
import com.hshar.tesserakt.model.Syndicate
import com.hshar.tesserakt.model.SyndicateMember
import com.hshar.tesserakt.model.User
import com.hshar.tesserakt.repository.DealRepository
import com.hshar.tesserakt.repository.RoleRepository
import com.hshar.tesserakt.repository.SyndicateRepository
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.type.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity.post
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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

    @Test
    fun getDealTest() {
        // Constants
        val dealId = UUID.randomUUID().toString()
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

        // Create a deal
        dealRepository.insert(deal)

        // Sign in
        val responseJson = this.mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
            .content("{\"usernameOrEmail\": \"${TESTCONSTS.permanentTestUsername}\", \"password\": \"123123q\"}")
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk).andReturn().response.contentAsString
        val token = Gson().fromJson<JsonObject>(responseJson)["accessToken"].asString

        // Get Deal
        mvc.perform(get("/api/deal/$dealId").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)

        // Clean up
        syndicateRepository.delete(syndicate)
        dealRepository.delete(deal)
    }
}
