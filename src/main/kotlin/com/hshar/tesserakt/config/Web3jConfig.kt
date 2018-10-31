package com.hshar.tesserakt.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.quorum.Quorum

@Configuration
class Web3jConfig {

    @Bean
    fun quorumWeb3jProvider(): Web3j {
        val httpService = HttpService("https://u0u7k2nevz-u0dn5c74nw-rpc.us-east-2.kaleido.io")
        httpService.addHeader("Authorization", "Basic dTBxdjdqbHF1bDpGbWVzR1N4X1gtVkRrbnFFUS1iOUZGVHhOLUEyd3M1a0hnb1ZKT19POE00")
        return Quorum.build(httpService)
    }
}
