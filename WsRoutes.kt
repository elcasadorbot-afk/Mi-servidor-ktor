package com.elcasador.web

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.server.request.*
import com.elcasador.config.AppConfig
import com.elcasador.services.AuthService
import kotlinx.coroutines.channels.consumeEach
import java.time.Duration

object WsRoutes {
    fun register(app: Application, config: AppConfig) {
        val auth = AuthService(config)
        app.install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(30)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        app.routing {
            webSocket("/ws") {
                val token = call.request.queryParameters["token"] ?: call.request.headers["x-api-key"]
                if (!auth.checkToken(token)) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid token"))
                    return@webSocket
                }

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            outgoing.send(Frame.Text("ack:$text"))
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
