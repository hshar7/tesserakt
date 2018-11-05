package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.File
import org.springframework.data.mongodb.repository.MongoRepository

interface FileRepository : MongoRepository<File, String> {
    fun findOneByFileName(id: String): File
}
