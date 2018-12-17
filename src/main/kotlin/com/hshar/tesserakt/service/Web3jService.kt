package com.hshar.tesserakt.service

import com.hshar.tesserakt.contract.DealLedger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.quorum.tx.ClientTransactionManager
import org.web3j.tuples.generated.Tuple5
import java.math.BigInteger

@Service
class Web3jService {

    @Autowired
    lateinit var web3jQuorum: Web3j

    companion object {
        const val CONTRACT_ADDRESS = "0xE718dd5406B8b981A0992678002f78215bE1a1db"
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

    fun loadDealLedgerContract(): DealLedger {
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

    fun getDealStatus(dealId: String): String {
        return loadDealLedgerContract().getDealStatus(dealId).send()
    }

    fun getDealSummary(dealId: String): Tuple5<String, String, String, String, BigInteger> {
        return loadDealLedgerContract().getDealSummary(dealId).send()
    }
}
