package br.com.webbudget

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
abstract class AbstractControllerTest : AbstractTest() {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    protected fun objectToJson(payload: Any): String = objectMapper.writeValueAsString(payload)

    protected fun <T> jsonToObject(json: String, valueType: Class<T>): T {
        return objectMapper.readValue(json, valueType)
    }
}