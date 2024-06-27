package ru.morsianin_shop

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.CommonHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.morsianin_shop.plugins.*
import ru.morsianin_shop.routes.*
import ru.morsianin_shop.storage.configureStorage

val CANCEL_DURATION_KV_ID: Long = 4123;

@CommandHandler(["/start"])
suspend fun start(user: User, bot: TelegramBot) {
    message { "Hello, what's your name?" }.send(user, bot)
    bot.inputListener[user] = "conversation"
}

@CommonHandler.Regex("blue colo?r")
suspend fun color(user: User, bot: TelegramBot) {
    message { "Oh you also like blue color?" }.send(user, bot)
}

fun main() {
    val ip = System.getenv("LISTEN_IP") ?: "127.0.0.1"
    val port = System.getenv("LISTEN_PORT")?.toIntOrNull() ?: 8080
    val timeoutRequest = System.getenv("TIMEOUT_REQUEST")?.toIntOrNull() ?: 360
    val timeoutResponse = System.getenv("TIMEOUT_RESPONSE")?.toIntOrNull() ?: 36

    val bot = TelegramBot(System.getenv("TG_BOT_TOKEN"))


    embeddedServer(Netty, port = port, host = ip, module = Application::module, configure = {
        connectionGroupSize = 8
        workerGroupSize = 16
        callGroupSize = 16
        shutdownGracePeriod = 2000
        shutdownTimeout = 6000
        requestQueueLimit = 32
        shareWorkGroup = false
        requestReadTimeoutSeconds = timeoutRequest
        responseWriteTimeoutSeconds = timeoutResponse
    }).start(wait = true)
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
    labelRoutes()
    KVRoutes()
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
