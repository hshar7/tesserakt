package com.hshar.tesserakt.repository

import com.hshar.tesserakt.model.Role
import com.hshar.tesserakt.type.RoleName
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface RoleRepository : MongoRepository<Role, Long> {
    fun findByName(roleName: RoleName): Optional<Role>
}