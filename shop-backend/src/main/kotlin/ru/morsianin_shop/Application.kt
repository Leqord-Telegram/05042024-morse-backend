package ru.morsianin_shop

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import ru.morsianin_shop.mapping.Mapper.mapToDTO
import ru.morsianin_shop.plugins.configureOther
import ru.morsianin_shop.plugins.configureRateLimit
import ru.morsianin_shop.plugins.configureResources
import ru.morsianin_shop.plugins.configureRouting
import ru.morsianin_shop.routes.*
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.DatabaseStorage.getDatabase
import ru.morsianin_shop.storage.StoredCategory
import ru.morsianin_shop.storage.StoredImage
import ru.morsianin_shop.storage.configureStorage

fun main() {
    val ip = System.getenv("LISTEN_IP") ?: "127.0.0.1"
    val port = System.getenv("LISTEN_PORT")?.toIntOrNull() ?: 8080

    embeddedServer(Netty, port = port, host = ip, module = Application::module, configure = {
        connectionGroupSize = 2
        workerGroupSize = 5
        callGroupSize = 10
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
