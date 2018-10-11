package com.hshar.tesserakt.service

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.hshar.tesserakt.config.KmongoConfig
import com.hshar.tesserakt.model.Syndicate
import com.hshar.tesserakt.model.User
import com.hshar.tesserakt.repository.UserRepository
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

    @Autowired
    lateinit var userRepository: UserRepository

    fun findByMembership(user: User): List<Syndicate> {
        val list = kmongoConfig.KmongoDb().getCollection("syndicates").find(
            Syndicate::members elemMatch Document().append("user", DBRef("users", user.id))
        ).toList()

        return Gson().fromJson(list.json)
    }

    fun findPastSyndicateMembers(user: User): List<User> {
        val list = kmongoConfig.KmongoDb().getCollection("syndicates").find(
            Syndicate::members elemMatch Document().append("user", DBRef("users", user.id))
        ).toList()

        val memberList = mutableMapOf<String, User>()

        list.forEach{syndicate ->
            (syndicate["members"] as List<Document>).forEach{member ->
                val userId = (member["user"] as DBRef).id.toString()
                if (!memberList.containsKey(userId))
                    memberList[userId] = userRepository.findOneById(userId)
            }
        }

        memberList.remove(user.id)
        return Gson().fromJson(memberList.values.json)
    }
}
