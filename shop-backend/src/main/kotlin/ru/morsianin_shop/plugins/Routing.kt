package ru.morsianin_shop.plugins

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}", status = HttpStatusCode.BadRequest)
        }
        exception<Exception> { call, cause ->
            call.respondText("Fucked up ${cause.message}", status = HttpStatusCode.BadRequest)
        }
    }
    install(ContentNegotiation) {
        json()
    }
    log.info("Base routing initialized")
}
