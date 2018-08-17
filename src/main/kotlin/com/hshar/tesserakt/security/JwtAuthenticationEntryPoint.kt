package com.hshar.tesserakt.security

import mu.KLogging
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    companion object: KLogging()

    @Throws(IOException::class, ServletException::class)
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, e: AuthenticationException) {
        logger.error("Responding with unauthorized error. Message - {}", e.message)
    }
}
