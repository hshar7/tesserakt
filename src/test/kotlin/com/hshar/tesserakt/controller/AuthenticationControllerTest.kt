package com.hshar.tesserakt.controller

import com.hshar.tesserakt.Exception.ResourceNotFoundException
import com.hshar.tesserakt.model.SignUpToken
import com.hshar.tesserakt.repository.RoleRepository
import com.hshar.tesserakt.repository.SignUpTokenRepository
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.type.RoleName
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var signUpTokenRepository: SignUpTokenRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun signInTest() {
        this.mvc.perform(post("/api/auth/signin").content("{\"usernameOrEmail\": \"admin\","
                + "\"password\": \"123123q\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk)
    }

    @Test
    fun signUpTest() {
        // Choose username and password
        val username = UUID.randomUUID().toString().substring(0, 14)
        val password = "123123q"
        val email = "$username@tesserakt.io"
        val token = UUID.randomUUID().toString().replace("-", "")
        val role = roleRepository.findByName(RoleName.ROLE_UNDERWRITER)
                .orElseThrow { ResourceNotFoundException("Role", "name", RoleName.ROLE_UNDERWRITER) }

        // Generate sign up token
        val tokenObject = SignUpToken(email, token, mutableSetOf(role), Date())
        signUpTokenRepository.insert(tokenObject)

        // actual sign up
        this.mvc.perform(post("/api/auth/signup").content(
                "{\"name\": \"test_user\","
                + "\"username\": \"$username\","
                + "\"email\": \"$email\","
                + "\"organizationName\": \"test_org\","
                + "\"password\": \"$password\","
                + "\"signUpToken\": \"$token\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated)

        // check if user is in database
        Assert.assertTrue(userRepository.existsByUsername(username))

        // Delete the objects we created
        userRepository.delete(userRepository.findByUsernameOrEmail(username, username))
    }
}
