package com.magnuen2k.dnsupdater.service

import com.magnuen2k.dnsupdater.CloudflareConfig
import com.magnuen2k.dnsupdater.DomainProperties
import com.magnuen2k.dnsupdater.dto.RecordResponse
import com.magnuen2k.dnsupdater.dto.RecordResult
import com.magnuen2k.dnsupdater.dto.ZoneResponse
import com.magnuen2k.dnsupdater.util.AuthConfig
import com.magnuen2k.dnsupdater.util.measureTime
import com.magnuen2k.dnsupdater.util.req
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class DnsService(
    private val config: CloudflareConfig,
    private val restTemplate: RestTemplate,
    @Value("\${poll.server}")
    private val pollServer: String,
) {

    val logger: Logger = LoggerFactory.getLogger(DnsService::class.java)
    val cache = mutableListOf<String>()

    @Scheduled(cron = "\${poll.cron}")
    fun updateDnsRecords() {
        measureTime("Scheduled DNS update service") {
            try {
                val ip = pollAddress().trim()
                if (cache.contains(ip)) {
                    logger.info("IP has not changed. No updating required.")
                    return
                }
                if (cache.count() > 2) cache.clear()
                cache.add(ip)

                for ((_, domain) in config.domains) {
                    logger.info("Updating DNS records for domain: ${domain.domain} with IP: $ip")
                    doUpdate(ip, domain)
                }
            } catch (e: Exception) {
                logger.error("Update failed with error message: ${e.message}")
            }
        }
    }

    private fun doUpdate(ip: String, domain: DomainProperties) {
        val auth = AuthConfig(
            apiKey = domain.apiKey,
            email = domain.apiEmail
        )
        val zones = domain.getZones(auth)
        for (zone in zones) {
            val records = domain.getRecords(zone, auth)
            for (record in records) {
                try {
                    val url = "${config.apiUrl}/zones/${zone}/dns_records/${record.id}"
                    val body = mapOf("type" to "A", "name" to record.name, "content" to ip, "proxied" to record.proxied)
                    restTemplate.req<Any>(auth, url, HttpMethod.PUT, body)
                    logger.info("Updated DNS record $record in zone $zone to IP $ip")
                } catch (e: Exception) {
                    logger.error("Failed to update record $record in zone $zone: ${e.message}")
                }
            }
        }
    }

    private fun DomainProperties.getRecords(zoneId: String, auth: AuthConfig): List<RecordResult> {
        return restTemplate.req<RecordResponse>(
            auth,
            "${config.apiUrl}/zones/$zoneId/dns_records?type=A",
        )
            .result
            .filter {
                if (records.isNotEmpty()) {
                    records.contains(it.name) && it.type == "A"
                } else it.type == "A"
            }
    }

    private fun DomainProperties.getZones(auth: AuthConfig): List<String> {
        return zones.ifEmpty {
            restTemplate.req<ZoneResponse>(
                auth,
                "${config.apiUrl}/zones?name=${this.domain}",
            ).result.map { it.id }
        }
    }

    private fun pollAddress(): String =
        restTemplate.getForObject(pollServer, String::class.java) ?: ""
}