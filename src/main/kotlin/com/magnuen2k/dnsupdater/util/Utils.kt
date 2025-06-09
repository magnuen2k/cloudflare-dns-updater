package com.magnuen2k.dnsupdater.util

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestTemplate
import java.net.URI

data class AuthConfig(
    val apiKey: String,
    val email: String
)

inline fun <reified T> RestTemplate.req(
    authConfig: AuthConfig,
    url: String,
    method: HttpMethod = HttpMethod.GET,
    body: Map<String, Any>? = null
): T = try {
    val headers = HttpHeaders().apply {
        set("X-Auth-Email", authConfig.email)
        set("Authorization", "Bearer ${authConfig.apiKey}")
        accept = listOf(MediaType.APPLICATION_JSON)
    }

    val requestEntity = when (body) {
        null -> RequestEntity<Any>(headers, method, URI(url))
        else -> RequestEntity(body, headers, method, URI(url))
    }

    val responseEntity = exchange(requestEntity, T::class.java)
    responseEntity.body!!
} catch (e: Exception) {
    println("Request failed with error message: ${e.message}")
    throw e
}