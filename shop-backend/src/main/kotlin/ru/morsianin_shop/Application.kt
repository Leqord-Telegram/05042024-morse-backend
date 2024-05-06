package ru.morsianin_shop

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.morsianin_shop.plugins.configureOther
import ru.morsianin_shop.plugins.configureRateLimit
import ru.morsianin_shop.plugins.configureResources
import ru.morsianin_shop.plugins.configureRouting
import ru.morsianin_shop.routes.*

fun main() {
    val ip = System.getenv("LISTEN_IP") ?: "127.0.0.1"
    val port = System.getenv("LISTEN_PORT").toIntOrNull() ?: 8080

    embeddedServer(Netty, port = port, host = ip, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureResources()
    configureRouting()

    configureRateLimit(searchRateLimit = System.getenv("RATE_SEARCH").toIntOrNull())

    productRoutes()
    orderRoutes()
    imageRoutes()
    categoryRoutes()
    userRoutes()
    configureOther()
}
