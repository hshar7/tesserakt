package com.hshar.tesserakt.Exception

class ResourceNotFoundException : RuntimeException {
    var resourceName: String
    var fieldName: String
    var fieldValue: Any

    constructor(resourceName: String, fieldName: String, fieldValue: Any) {
        this.resourceName = resourceName
        this.fieldName = fieldName
        this.fieldValue = fieldValue
    }
}