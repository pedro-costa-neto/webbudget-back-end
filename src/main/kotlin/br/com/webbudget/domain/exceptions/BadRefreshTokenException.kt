package br.com.webbudget.domain.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseBody
@ResponseStatus(HttpStatus.UNAUTHORIZED)
class BadRefreshTokenException(message: String) : RuntimeException(message)
