package com.magnuen2k.dnsupdater

import com.magnuen2k.dnsupdater.dto.RecordResponse
import com.magnuen2k.dnsupdater.dto.ZoneResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class DnsUpdater(
    @Value("\${cloudflare.domain}")
    private val domain: String,
    @Value("\${cloudflare.api.url}")
    private val apiUrl: String,
    @Value("\${cloudflare.api.key}")
    private val apiKey: String,
    @Value("\${cloudflare.api.email}")
    private val email: String,
    @Value("\${poll.server}")
    private val pollServer: String,
    private val restTemplate: RestTemplate,
) {
    val cache = mutableListOf("")

    @Scheduled(cron = "\${poll.cron}")
    fun run() {
        try {
            val ip = pollAddress().trim()
            if (cache.isNotEmpty() && cache.contains(ip)) return
            if (cache.count() > 10) cache.clear()
            cache.apply { add(ip) }

            webRequest<ZoneResponse>("$apiUrl/zones?name=$domain", HttpMethod.GET)
                ?.result
                ?.forEach { zone ->
                    webRequest<RecordResponse>(
                        url = "$apiUrl/zones/${zone.id}/dns_records",
                        method = HttpMethod.GET
                    )
                        ?.result
                        ?.forEach { record ->
                            if (record.type == "A") {
                                webRequest<Any>(
                                    url = "$apiUrl/zones/${record.zone_id}/dns_records/${record.id}",
                                    method = HttpMethod.PUT,
                                    body = mapOf(
                                        "content" to ip,
                                        "type" to "A",
                                        "name" to record.name,
                                        "proxied" to record.proxied,
                                    )
                                )
                            }
                        }
                }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private inline fun <reified T> webRequest(url: String, method: HttpMethod, body: Map<String, Any>? = null): T? {
        println("Calling $url with $method")
        val headers = HttpHeaders().apply {
            set("X-Auth-Email", email)
            set("Authorization", "Bearer $apiKey")
            accept = listOf(MediaType.APPLICATION_JSON)
        }

        val requestEntity = when (body) {
            null -> RequestEntity<Any>(headers, method, URI(url))
            else -> RequestEntity(body, headers, method, URI(url))
        }

        val responseEntity = restTemplate.exchange(requestEntity, T::class.java)
        return responseEntity.body ?: null
    }

    fun pollAddress(): String {
        println("polling ip...")
        return restTemplate.getForObject(pollServer, String::class.java) ?: ""
    }
}