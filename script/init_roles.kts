#!/usr/bin/env kscript
@file:DependsOn("org.mongodb:mongo-java-driver:3.8.2")
import java.util.UUID
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoClient
import com.mongodb.MongoClientSettings
import com.mongodb.ConnectionString
import com.mongodb.ServerAddress
import com.mongodb.MongoCredential
import com.mongodb.MongoClientOptions
import com.mongodb.client.model.IndexOptions

var mongoUrl: String = System.getenv("MONGO_URL") ?: "mongodb://localhost:27017/tesserakt"
val client = MongoClients.create(mongoUrl)
val database = client.getDatabase("tesserakt")
val roles = database.getCollection("roles")

var index = org.bson.Document("name", 1)
roles.createIndex(index, IndexOptions().unique(true));

val lender = org.bson.Document()
lender.append("_id", UUID.randomUUID().toString())
lender.append("name", "ROLE_LENDER")
roles.insertOne(lender)

val underwriter = org.bson.Document()
underwriter.append("_id", UUID.randomUUID().toString())
underwriter.append("name", "ROLE_UNDERWRITER")
roles.insertOne(underwriter)

val ratingAgency = org.bson.Document()
ratingAgency.append("_id", UUID.randomUUID().toString())
ratingAgency.append("name", "ROLE_RATING_AGENCY")
roles.insertOne(ratingAgency)

val admin = org.bson.Document()
admin.append("_id", UUID.randomUUID().toString())
admin.append("name", "ROLE_ADMIN")
roles.insertOne(admin)
