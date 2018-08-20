package com.hshar.tesserakt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TesseraktApplication

// TODO: Enable Mongo Auditing
fun main(args: Array<String>) {
    runApplication<TesseraktApplication>(*args)
}
