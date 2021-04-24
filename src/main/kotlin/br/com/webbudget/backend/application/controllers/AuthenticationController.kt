package br.com.webbudget.backend.application.controllers

import br.com.webbudget.backend.application.payloads.Credential
import br.com.webbudget.backend.application.payloads.Token
import br.com.webbudget.backend.domain.services.AuthenticationService
import br.com.webbudget.backend.domain.services.TokenService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/authentication")
class AuthenticationController(
    private val tokenService: TokenService,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid credential: Credential): ResponseEntity<Token> {

        val authentication = this.authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(credential.username, credential.password)
        )

        val authenticable = authentication.principal as AuthenticationService.AuthenticableUser

        val token = tokenService.generateFrom(authenticable.username)
        return ResponseEntity.ok(token)
    }

    @PostMapping("/logout")
    fun logout(@RequestBody token: String): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @PutMapping("/refresh")
    fun refresh(@RequestBody token: Token): ResponseEntity<Token> {
        return ResponseEntity.ok().build()
    }
}