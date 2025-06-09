package com.magnuen2k.dnsupdater

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.client.RestTemplate


@ConfigurationProperties(prefix = "cloudflare")
class CloudflareConfig {
    var apiUrl: String = ""
    var domains: Map<String, DomainProperties> = mutableMapOf()
}

class DomainProperties(
    var domain: String = "",
    var apiKey: String = "",
    var apiEmail: String = "",
    var zones: List<String> = emptyList(),
    var records: List<String> = emptyList()
)

@Configuration
@EnableConfigurationProperties(CloudflareConfig::class)
class ConfigProperties

@Configuration
class Config {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Primary
    @Bean
    fun cloudflareConfig(config: CloudflareConfig): CloudflareConfig {
        return config
    }
}