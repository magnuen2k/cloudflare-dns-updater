package com.magnuen2k.dnsupdater.dto

data class RecordResponse (
    val result: List<RecordResult>
)

data class RecordResult (
    val id: String,
    val name: String,
    val type: String,
    val proxied: Boolean,
)