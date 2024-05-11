package ru.morsianin_shop

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.morsianin_shop.plugins.*
import ru.morsianin_shop.routes.*
import ru.morsianin_shop.storage.configureStorage

fun main() {
    val ip = System.getenv("LISTEN_IP") ?: "127.0.0.1"
    val port = System.getenv("LISTEN_PORT")?.toIntOrNull() ?: 8080

    embeddedServer(Netty, port = port, host = ip, module = Application::module, configure = {
        connectionGroupSize = 4
        workerGroupSize = 8
        callGroupSize = 16
        shutdownGracePeriod = 2000
        shutdownTimeout = 3000
        requestQueueLimit = 16
        shareWorkGroup = false
        responseWriteTimeoutSeconds = 10
    })
        .start(wait = true)
}

fun Application.module() {
    configureStorage()
    configureResources()
    configureAuth()
    configureRouting()
    configureOther()

    configureRateLimit()

    productRoutes()
    imageRoutes()
    categoryRoutes()
    userRoutes()
    searchRoutes()
    orderRoutes()
    cartRoutes()
    authRoutes()
}
