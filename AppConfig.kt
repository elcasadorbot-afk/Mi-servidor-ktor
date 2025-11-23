package com.elcasador.config

import java.lang.System.getenv

data class AppConfig(
    val port: Int,
    val adminToken: String,
    val externalApi: String,
    val cacheTtlMillis: Long,
    val rateLimitPerMinute: Int
) {
    companion object {
        fun load(): AppConfig {
            val port = getenv("PORT")?.toInt() ?: 8080
            val adminToken = getenv("ADMIN_TOKEN") ?: "change-me"
            val externalApi = getenv("EXTERNAL_API") ?: "https://ipwhois.app/json/"
            val cacheTtl = getenv("CACHE_TTL_SECONDS")?.toLong()?.times(1000) ?: 45_000L
            val rate = getenv("RATE_PER_MIN")?.toInt() ?: 120
            return AppConfig(port, adminToken, externalApi, cacheTtl, rate)
        }
    }
}
