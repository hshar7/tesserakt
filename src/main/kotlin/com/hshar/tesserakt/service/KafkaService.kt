package com.hshar.tesserakt.service

import com.google.gson.Gson
import com.hshar.tesserakt.model.Deal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaService {

    companion object {
        const val newDealTopic = "streaming.deals.newDeals"
    }

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    fun sendNewDeal(deal: Deal) {
        kafkaTemplate.send(newDealTopic, Gson().toJson(deal))
    }
}
