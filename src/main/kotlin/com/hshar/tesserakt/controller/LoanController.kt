package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hshar.tesserakt.model.Loan
import com.hshar.tesserakt.repository.LoanRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

@RestController
@RequestMapping("/api")
class LoanController {
    @Autowired
    lateinit var loanRepository: LoanRepository

    @GetMapping("/loan")
    fun getLoan(id: String): Loan {
        return loanRepository.findOneByUuid(id)
    }

    @PostMapping("/loan")
    fun postLoan(@RequestBody body: String): Loan {
        val loan = Gson().fromJson<JsonObject>(body)
        loan["uuid"] = UUID.randomUUID().toString()
        loan["status"] = "live"
        loan["createdAt"] = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        loan["updatedAt"] = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        return loanRepository.insert(Gson().fromJson<Loan>(loan))
    }
}
