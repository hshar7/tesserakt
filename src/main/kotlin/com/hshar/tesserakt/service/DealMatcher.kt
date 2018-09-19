package com.hshar.tesserakt.service

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.hshar.tesserakt.model.Deal
import com.hshar.tesserakt.model.MatchingCriteria
import com.hshar.tesserakt.model.User
import com.mongodb.DBRef
import org.litote.kmongo.*
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class DealMatcher {

    @KafkaListener(topics = ["mytopic"], groupId = "mygroup")
    fun listen(message: String) {

        val client = KMongo.createClient()
        val database = client.getDatabase("tesserakt")
        val col = database.getCollection("matching_criteria")
        val deal = Gson().fromJson<Deal>(message)

        val matchingCriterias = col.find(
            MatchingCriteria::jurisdiction contains deal.jurisdiction,
            MatchingCriteria::capitalAmountMin lte deal.capitalAmount,
            MatchingCriteria::capitalAmountMax gte deal.capitalAmount,
            MatchingCriteria::interestRateMin lte deal.interestRate,
            MatchingCriteria::interestRateMax gte deal.interestRate,
            MatchingCriteria::loanType contains deal.loanType,
            MatchingCriteria::maturityMin lte deal.maturity,
            MatchingCriteria::maturityMax gte deal.maturity
        )
        println("deal: " + deal.toString())
        matchingCriterias.forEach {
            if ((it["user"] as DBRef).id != deal.underwriter.id) {
                println(it.toString())
            }
        }
    }
}
