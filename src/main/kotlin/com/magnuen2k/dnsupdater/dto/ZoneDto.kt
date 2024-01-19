package com.magnuen2k.dnsupdater.dto

data class ZoneResponse (
    val result: List<ZoneResult>
)

data class ZoneResult (
    val id: String,
    val name: String,
)