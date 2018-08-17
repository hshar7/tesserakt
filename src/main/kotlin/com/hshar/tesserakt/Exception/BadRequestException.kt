package com.hshar.tesserakt.Exception

class BadRequestException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}