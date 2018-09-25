package com.hshar.tesserakt.service

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.hshar.tesserakt.Exception.ResourceNotFoundException
import com.hshar.tesserakt.config.KmongoConfig
import com.hshar.tesserakt.model.Deal
import com.hshar.tesserakt.model.MatchingCriteria
import com.hshar.tesserakt.model.Notification
import com.hshar.tesserakt.repository.NotificationRepository
import com.hshar.tesserakt.repository.UserRepository
import com.mongodb.DBRef
import com.mongodb.MongoClientURI
import org.litote.kmongo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.util.*

@Service
class DealMatcher {

    @Autowired
    lateinit var notificationRepository: NotificationRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var kmongoConfig: KmongoConfig

    @KafkaListener(topics = ["streaming.deals.newDeals"], groupId = "tesserakt")
    fun listen(message: String) {

        val col = kmongoConfig.KmongoDb().getCollection("matching_criteria")
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
