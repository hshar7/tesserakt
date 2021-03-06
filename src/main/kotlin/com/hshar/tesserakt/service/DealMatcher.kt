package com.hshar.tesserakt.service

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.hshar.tesserakt.exception.ResourceNotFoundException
import com.hshar.tesserakt.config.KmongoConfig
import com.hshar.tesserakt.model.Deal
import com.hshar.tesserakt.model.MatchingCriteria
import com.hshar.tesserakt.model.Notification
import com.hshar.tesserakt.repository.NotificationRepository
import com.hshar.tesserakt.repository.UserRepository
import com.mongodb.DBRef
import org.litote.kmongo.gte
import org.litote.kmongo.lte
import org.litote.kmongo.contains
import org.litote.kmongo.find
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class DealMatcher {

    @Autowired
    lateinit var notificationRepository: NotificationRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var kmongoConfig: KmongoConfig

    suspend fun checkMatchup(message: String) {

        val col = kmongoConfig.kMongoDb().getCollection("matching_criteria")
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

        matchingCriterias.forEach {
            if ((it["user"] as DBRef).id != deal.underwriter.id) {

                val user = userRepository.findById((it["user"] as DBRef).id.toString())
                    .orElseThrow { ResourceNotFoundException("User", "id", (it["user"] as DBRef).id) }

                notificationRepository.insert(Notification(
                    UUID.randomUUID().toString(),
                    user,
                    "New deal matching your criteria",
                    "/market/${deal.id}",
                    Date()
                ))
            }
        }
    }
}
