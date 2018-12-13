#!/usr/bin/env kscript
@file:DependsOn("org.mongodb:mongo-java-driver:3.8.2")
@file:DependsOn("org.springframework.security:spring-security-crypto:5.1.1.RELEASE")
@file:DependsOn("org.mindrot:jbcrypt:0.3m")
import java.util.UUID
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoClient
import com.mongodb.MongoClientSettings
import com.mongodb.ConnectionString
import com.mongodb.ServerAddress
import com.mongodb.MongoCredential
import com.mongodb.MongoClientOptions
import com.mongodb.client.model.IndexOptions
import org.mindrot.jbcrypt.BCrypt
import com.mongodb.client.model.Filters.*
import com.mongodb.DBRef

var mongoUrl: String = "mongodb://tesserakt:123123q@ds157539.mlab.com:57539/tesserakt-test"
val client = MongoClients.create(mongoUrl)
val database = client.getDatabase("tesserakt-test")
val users = database.getCollection("users")

var index = org.bson.Document("email", 1)
users.createIndex(index, IndexOptions().unique(true));

// Find the role
val adminRole = database.getCollection("roles").find(eq("name", "ROLE_ADMIN")).first()

val lender = org.bson.Document()
lender.append("_id", UUID.randomUUID().toString())
lender.append("name", "admin")
lender.append("username", "admin")
lender.append("email", "admin@tesserakt.com")
lender.append("organizationName", "Tesserakt")
lender.append("password", BCrypt.hashpw("123123q", BCrypt.gensalt()))
lender.append("roles", setOf(DBRef("roles", adminRole.getString("_id"))))
users.insertOne(lender)
