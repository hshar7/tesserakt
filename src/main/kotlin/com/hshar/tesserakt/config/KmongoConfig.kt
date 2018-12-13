package com.hshar.tesserakt.config

import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KmongoConfig {

    @Value("\${spring.data.mongodb.uri}")
    lateinit var mongoUrl: String

    @Value("\${database.name}")
    lateinit var databaseName: String

    @Bean
    fun KmongoDb(): MongoDatabase {
        val client = KMongo.createClient(MongoClientURI(mongoUrl))
        val database = client.getDatabase(databaseName)

        return database
    }
}
