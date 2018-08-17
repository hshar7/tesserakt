package com.hshar.tesserakt.Exception

class AppException : RuntimeException {
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(message: String?) : super(message)
}
