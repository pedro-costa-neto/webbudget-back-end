package br.com.webbudget.controllers

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.payloads.configuration.Credential
import br.com.webbudget.application.payloads.configuration.RefreshCredential
import br.com.webbudget.application.payloads.configuration.Token
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class AuthenticationControllerTest : BaseControllerIntegrationTest() {

    @Value("\${web-budget.jwt.access-token-expiration}")
    private var secondsToExpireToken: Int? = 0

    @Test
    fun `should be ok when valid credentials`() {
        mockMvc.post("$ENDPOINT_URL/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectToJson(Credential("admin@webbudget.com.br", "admin"))
        }.andExpect {
            status { isOk() }
        }.andExpect {
            jsonPath("$.id", notNullValue())
            jsonPath("$.accessToken", notNullValue())
            jsonPath("$.refreshToken", notNullValue())
            jsonPath("$.expireIn", `is`(secondsToExpireToken))
        }
    }

    @Test
    fun `should be unauthorized when invalid credentials`() {
        mockMvc.post("$ENDPOINT_URL/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectToJson(Credential("baduser@webbudget.com.br", "admin"))
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should refresh token`() {
        val oldTokenJson = mockMvc.post("$ENDPOINT_URL/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectToJson(Credential("admin@webbudget.com.br", "admin"))
        }.andExpect {
            status { isOk() }
        }.andExpect {
            jsonPath("$.accessToken", notNullValue())
            jsonPath("$.refreshToken", notNullValue())
        }.andReturn()
            .response
            .contentAsString

        val oldToken = jsonToObject(oldTokenJson, Token::class.java)
        val refreshCredential = RefreshCredential(oldToken.id, "admin@webbudget.com.br", oldToken.refreshToken)

        val newTokenJson = mockMvc.post("$ENDPOINT_URL/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = objectToJson(refreshCredential)
        }.andExpect {
            status { isOk() }
        }.andExpect {
            jsonPath("$.accessToken", notNullValue())
            jsonPath("$.refreshToken", notNullValue())
        }.andReturn()
            .response
            .contentAsString

        val newToken = jsonToObject(newTokenJson, Token::class.java)
        assertThat(newToken.id).isNotEqualTo(oldToken.id)
        assertThat(newToken.accessToken).isNotEqualTo(oldToken.accessToken)
        assertThat(newToken.refreshToken).isNotEqualTo(oldToken.refreshToken)
    }

    companion object {
        private const val ENDPOINT_URL = "/authentication/"
    }
}
