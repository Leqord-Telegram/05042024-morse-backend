package ru.morsianin_shop

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.ratelimit.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.morsianin_shop.plugins.configureOther
import ru.morsianin_shop.plugins.configureRateLimit
import ru.morsianin_shop.plugins.configureResources
import ru.morsianin_shop.plugins.configureRouting
import ru.morsianin_shop.routes.*
import ru.morsianin_shop.storage.DatabaseSingleton
import ru.morsianin_shop.storage.configureStorage

fun main() {
    val ip = System.getenv("LISTEN_IP") ?: "127.0.0.1"
    val port = System.getenv("LISTEN_PORT")?.toIntOrNull() ?: 8080

    embeddedServer(Netty, port = port, host = ip, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureStorage()
    configureResources()
    configureRouting()
    configureOther()

    configureRateLimit()

    productRoutes()
    orderRoutes()
    imageRoutes()
    categoryRoutes()
    userRoutes()
    searchRoutes()
}
