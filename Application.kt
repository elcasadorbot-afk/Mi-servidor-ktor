package com.elcasador

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.elcasador.config.AppConfig
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.logging.*
import com.elcasador.web.RestRoutes
import com.elcasador.web.WsRoutes
import kotlinx.coroutines.*

fun main() {
    val config = AppConfig.load()

    embeddedServer(Netty, port = config.port) {
        install(ContentNegotiation) {
            json()
        }
        install(Logging)

        // Start background workers
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        scope.launch {
            while (isActive) {
                try {
                    kotlinx.coroutines.delay(config.cacheTtlMillis)
                } catch (e: Throwable) {
                }
            }
        }

        RestRoutes.register(this, config)
        WsRoutes.register(this, config)
    }.start(wait = true)
}
