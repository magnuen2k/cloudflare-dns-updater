package com.magnuen2k.dnsupdater

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DnsUpdaterApplication

fun main(args: Array<String>) {
    runApplication<DnsUpdaterApplication>(*args)
}
