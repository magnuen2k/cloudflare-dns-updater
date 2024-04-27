package com.magnuen2k.dnsupdater.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.measureTimedValue

inline fun <T> Any.measureTime(message: String, block: () -> T): T {
    val log: Logger = LoggerFactory.getLogger(javaClass)
    log.info("Start measure time: $message")
    val (value, elapsed) = measureTimedValue {
        block()
    }
    log.info("Finished measure time, elapsed=$elapsed: $message")
    return value
}