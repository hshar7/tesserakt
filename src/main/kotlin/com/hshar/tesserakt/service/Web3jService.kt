package com.hshar.tesserakt.service

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hshar.tesserakt.contract.DealLedger
import com.hshar.tesserakt.model.Deal
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.web3j.protocol.Web3j
import org.web3j.quorum.tx.ClientTransactionManager
import org.web3j.tuples.generated.Tuple5
import java.math.BigInteger

@Service
class Web3jService {

    @Autowired
    lateinit var web3jQuorum: Web3j

    companion object {
        const val CONTRACT_ADDRESS = "0x98a3bc206185a0770C4A847CD082FbB355dB08aE"
        const val GAS_PRICE = 200
        const val GAS_LIMIT = 4500000
    }

    fun deployDealLedgerContract() {
        val transactionManager = ClientTransactionManager(
                web3jQuorum,
                "0x62c4a9d5e93aa41c4b9acfce8a6a9d634f097b73",
                null,
                emptyList()
        )

        DealLedger.deploy(web3jQuorum, transactionManager, GAS_PRICE.toBigInteger(), GAS_LIMIT.toBigInteger()).send()
    }

    fun sendNewDealAsync(deal: Deal) {
        loadDealLedgerContract().addDeal(
                deal.id,
                deal.underwriter.id,
                Web3jService.CONTRACT_ADDRESS,
                deal.borrowerName,
                deal.jurisdiction.toString(),
                deal.capitalAmount.toString(),
                deal.interestRate.toString(),
                deal.loanType.toString(),
                deal.maturity.toBigInteger(),
                deal.assetClass.toString(),
                deal.assetRating.toString(),
                Gson().toJson(deal.syndicate),
                deal.status.toString(),
                "{}"
        ).sendAsync()
    }

    fun sendDealUpdate(deal: Deal) {
        loadDealLedgerContract().updateDeal(
                deal.id,
                deal.underwriter.id,
                Web3jService.CONTRACT_ADDRESS,
                deal.borrowerName,
                deal.jurisdiction.toString(),
                deal.capitalAmount.toString(),
                deal.interestRate.toString(),
                deal.loanType.toString(),
                deal.maturity.toBigInteger(),
                deal.assetClass.toString(),
                deal.assetRating.toString(),
                Gson().toJson(deal.syndicate),
                deal.status.toString()
        ).sendAsync()
    }

    fun addDocumentHash(dealId: String, document: MultipartFile, documentName: String) {
        val documentHashesJson = loadDealLedgerContract().getDocumentHashes(dealId).send()
        val documentHashesObject = Gson().fromJson<JsonObject>(documentHashesJson)

        documentHashesObject[documentName] = DigestUtils.sha256Hex(document.bytes)
        loadDealLedgerContract().updateDocumentHashes(dealId, Gson().toJson(documentHashesObject)).sendAsync()
    }

    fun removeDocumentHash(dealId: String, documentName: String) {
        val documentHashesJson = loadDealLedgerContract().getDocumentHashes(dealId).send()
        val documentHashesObject = Gson().fromJson<JsonObject>(documentHashesJson)

        documentHashesObject.remove(documentName)
        loadDealLedgerContract().updateDocumentHashes(dealId, Gson().toJson(documentHashesJson)).sendAsync()
    }

    fun getDealStatus(dealId: String): String {
        return loadDealLedgerContract().getDealStatus(dealId).send()
    }

    fun getDealSummary(dealId: String): Tuple5<String, String, String, String, BigInteger> {
        return loadDealLedgerContract().getDealSummary(dealId).send()
    }

    private fun loadDealLedgerContract(): DealLedger {
        val transactionManager = ClientTransactionManager(
                web3jQuorum,
                "0x62c4a9d5e93aa41c4b9acfce8a6a9d634f097b73",
                null,
                emptyList()
        )

        return DealLedger.load(
                CONTRACT_ADDRESS,
                web3jQuorum,
                transactionManager,
                GAS_PRICE.toBigInteger(),
                GAS_LIMIT.toBigInteger()
        )
    }
}
