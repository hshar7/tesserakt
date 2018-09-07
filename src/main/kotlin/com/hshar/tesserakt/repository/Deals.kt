package com.hshar.tesserakt.repository

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Deals {

    @Autowired
    lateinit var dealRepository: DealRepository

    @Autowired
    lateinit var syndicateRepository: SyndicateRepository

    fun getDealsByStatus(status: String): JsonArray {
        val deals = JsonArray()
        dealRepository.findByStatus(status).forEach {
            val dealJson = Gson().fromJson<JsonObject>(Gson().toJson(it))
            val syndicate = syndicateRepository.findOneById(it.syndicateId)
                    .orElseThrow { NoSuchElementException("Cannot find the syndicate ${it.syndicateId}") }
            dealJson["syndicate"] = Gson().fromJson<JsonObject>(Gson().toJson(syndicate))
            deals.add(dealJson)
        }
        return deals
    }
}
