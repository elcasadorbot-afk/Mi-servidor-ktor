package com.elcasador.web

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import com.elcasador.config.AppConfig
import com.elcasador.services.GeoService
import com.elcasador.services.PriceService
import com.elcasador.services.ZoneService
import com.elcasador.services.AuthService
import io.ktor.http.*

object RestRoutes {
    fun register(app: Application, config: AppConfig) {
        val geo = GeoService(config)
        val price = PriceService()
        val zones = ZoneService()
        val auth = AuthService(config)

        app.routing {
            route("/api") {
                get("/health") {
                    call.respond(mapOf("status" to "ok"))
                }

                get("/geo") {
                    val ip = call.request.origin.remoteHost
                    val data = geo.getGeoForIp(if (ip == "127.0.0.1") null else ip)
                    call.respond(data)
                }

                post("/price") {
                    val body = call.receive<Map<String, Double>>()
                    val lat1 = body["lat1"] ?: 0.0
                    val lon1 = body["lon1"] ?: 0.0
                    val lat2 = body["lat2"] ?: 0.0
                    val lon2 = body["lon2"] ?: 0.0
                    val p = price.calculatePriceKm(lat1, lon1, lat2, lon2)
                    call.respond(mapOf("price" to p))
                }

                post("/zones/add") {
                    val token = call.request.headers["x-api-key"] ?: call.request.queryParameters["token"]
                    if (!auth.checkToken(token)) return@post call.respond(HttpStatusCode.Unauthorized)
                    val payload = call.receive<Map<String, Any>>()
                    call.respond(mapOf("ok" to true))
                }

                get("/zones/list") {
                    call.respond(zones.listZones())
                }
            }
        }
    }
}
