package com.hshar.tesserakt.service

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.hshar.tesserakt.config.KmongoConfig
import com.hshar.tesserakt.model.Syndicate
import com.hshar.tesserakt.model.User
import com.mongodb.DBRef
import org.bson.Document
import org.litote.kmongo.elemMatch
import org.litote.kmongo.json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SyndicateDAO {

    @Autowired
    lateinit var kmongoConfig: KmongoConfig

    fun findByMembership(user: User): List<Syndicate> {
        val list = kmongoConfig.KmongoDb().getCollection("syndicates").find(
            Syndicate::members elemMatch Document().append("user", DBRef("users", user.id))
        ).toList()

        return Gson().fromJson(list.json)
    }
}
