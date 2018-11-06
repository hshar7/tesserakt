package com.hshar.tesserakt.controller

import com.google.gson.Gson
import com.hshar.tesserakt.service.Web3jService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


// TODO: Either remove this or make use of it. If making use of it, add ROLES!
@RestController
@RequestMapping("/api")
class DealLedgerController {

    @Autowired
    lateinit var web3jService: Web3jService

    @PostMapping("/dealLedger/deploy")
    fun deployDealLedgerContract(): ResponseEntity<String> {
        web3jService.deployDealLedgerContract()

        return ResponseEntity("ok", HttpStatus.OK)
    }

    @GetMapping("/dealLedger/{dealId}/status")
    fun getDealStatus(@PathVariable dealId: String): String {
        return web3jService.getDealStatus(dealId)
    }

    @GetMapping("/dealLedger/{dealId}/summary")
    fun getDealSummary(@PathVariable dealId: String): String {
        return Gson().toJson(web3jService.getDealSummary(dealId))
    }
}
