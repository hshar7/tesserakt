package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hshar.tesserakt.model.MatchingCriteria
import com.hshar.tesserakt.repository.MatchingCriteriaRepository
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.security.CurrentUser
import com.hshar.tesserakt.security.UserPrincipal
import com.hshar.tesserakt.type.AssetClass
import com.hshar.tesserakt.type.AssetRating
import com.hshar.tesserakt.type.Jurisdiction
import com.hshar.tesserakt.type.LoanType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api")
class MatchingCriteriaController {
    @Autowired
    lateinit var matchingCriteriaRepository: MatchingCriteriaRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @GetMapping("/matchingCriteria/{id}")
    @PreAuthorize("hasRole('USER')")
    fun getMatchingCriteria(@PathVariable id: String): MatchingCriteria {
        return matchingCriteriaRepository.findOneById(id)
    }


    @GetMapping("/matchingCriteria")
    @PreAuthorize("hasRole('USER')")
    fun getMyMatchingCriteria(@CurrentUser userDetails: UserPrincipal): List<MatchingCriteria> {

        val user = userRepository.findByUsername(userDetails.username)

        return matchingCriteriaRepository.findByUser(user)
    }

    @PostMapping("/matchingCriteria")
    @PreAuthorize("hasRole('USER')")
    fun createMatchingCriteria(@RequestBody body: String, @CurrentUser userDetails: UserPrincipal): MatchingCriteria {
        val matchingCriteriaBody = Gson().fromJson<JsonObject>(body)
        val user = userRepository.findByUsername(userDetails.username)

        var jurisdictionList = mutableListOf<Jurisdiction>()
        if (matchingCriteriaBody["jurisdiction"].asString == "ANY") {
            jurisdictionList = jurisdictionList.plus(Jurisdiction.values()).toMutableList()
        } else {
            Jurisdiction.values().forEach {
                if (it.name == matchingCriteriaBody["jurisdiction"].asString) {
                    jurisdictionList.add(it)
                }
            }
        }

        var loanTypeList = mutableListOf<LoanType>()
        if (matchingCriteriaBody["loanType"].asString == "ANY") {
            loanTypeList = loanTypeList.plus(LoanType.values()).toMutableList()
        } else {
            LoanType.values().forEach {
                if (it.name == matchingCriteriaBody["loanType"].asString) {
                    loanTypeList.add(it)
                }
            }
        }

        var assetClassList = mutableListOf<AssetClass>()
        if (matchingCriteriaBody["assetClass"].asString == "ANY") {
            assetClassList = assetClassList.plus(AssetClass.values()).toMutableList()
        } else {
            AssetClass.values().forEach {
                if (it.name == matchingCriteriaBody["assetClass"].asString) {
                    assetClassList.add(it)
                }
            }
        }

        val assetRatingList = mutableListOf<AssetRating>()
        matchingCriteriaBody["assetRating"].asJsonArray.forEach {
           assetRatingList.add(AssetRating.valueOf(it.asString))
        }

        val matchingCriteria = MatchingCriteria(
            UUID.randomUUID().toString(),
            user,
            jurisdictionList,
            matchingCriteriaBody["capitalAmount"].asJsonArray[0].asFloat,
            matchingCriteriaBody["capitalAmount"].asJsonArray[1].asFloat,
            matchingCriteriaBody["interestRate"].asJsonArray[0].asFloat,
            matchingCriteriaBody["interestRate"].asJsonArray[1].asFloat,
            loanTypeList,
            matchingCriteriaBody["maturity"].asJsonArray[0].asInt,
            matchingCriteriaBody["maturity"].asJsonArray[1].asInt,
            assetClassList,
            assetRatingList,
            Date(),
            Date()
        )

        return matchingCriteriaRepository.insert(matchingCriteria)
    }
}
