package ru.morsianin_shop

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
        requestReadTimeoutSeconds = 120
        responseWriteTimeoutSeconds = 120
    })
        .start(wait = true)
}

// TODO: вынести конфишурацию в файл
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

    /*
    for (i in 1..20) {
        val runtime = Runtime.getRuntime()
        runtime.gc()
        val startMemory = runtime.totalMemory() - runtime.freeMemory()

        val duration = measureTimeMillis {
            runBlocking {
                SearchLevenshtein.findTopBestMatchingProducts("горбатая говно горбатая псина анус", 10)
            }
        }
        val endMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryUsed = endMemory - startMemory

        println("Использовано памяти: ${memoryUsed/(1024.0*1024.0)} МБайт")

        println("Duration: $duration ms")

    }
    */
}
