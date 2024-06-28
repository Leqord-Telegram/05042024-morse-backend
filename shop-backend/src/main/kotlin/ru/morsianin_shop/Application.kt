package ru.morsianin_shop

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.CommonHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.api.answer.answerCallbackQuery
import eu.vendeli.tgbot.api.chat.getChat
import eu.vendeli.tgbot.api.message.editMessageText
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.api.poll
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.chat.Chat
import eu.vendeli.tgbot.types.internal.CallbackQueryUpdate
import eu.vendeli.tgbot.types.internal.EditedMessageUpdate
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import eu.vendeli.tgbot.types.internal.UpdateType
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.statements.StatementType
import ru.morsianin_shop.plugins.*
import ru.morsianin_shop.routes.*
import ru.morsianin_shop.storage.configureStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

val CANCEL_DURATION_KV_ID: Long = 4123;
val ORDER_CHAT_ID: Long = -4277372202;

val bot = TelegramBot(System.getenv("TG_BOT_TOKEN"))


@CommandHandler(["/start"])
suspend fun start(user: User, bot: TelegramBot) {
    message { "Привет!" }.send(user, bot)
    message { "Нажми кнопку 'Магазин', чтобы открыть приложение" }.send(user, bot)

}

@CommonHandler.Regex("^cancel.*$", scope = [UpdateType.CALLBACK_QUERY])
suspend fun test(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
    message { "Нажал!" }.send(ORDER_CHAT_ID, bot)

    //update.callbackQuery.message?.messageId

    editMessageText { "Балжеж" }

    val id = update.callbackQuery.data!!.removePrefix("cancel").toLong()

    message { "ID: $id" }.send(ORDER_CHAT_ID, bot)
}

suspend fun main() {
    print("STARTUP AT: ${LocalDateTime.now().format(ISO_LOCAL_DATE_TIME)}")

    val ip = System.getenv("LISTEN_IP") ?: "127.0.0.1"
    val port = System.getenv("LISTEN_PORT")?.toIntOrNull() ?: 8080
    val timeoutRequest = System.getenv("TIMEOUT_REQUEST")?.toIntOrNull() ?: 360
    val timeoutResponse = System.getenv("TIMEOUT_RESPONSE")?.toIntOrNull() ?: 36

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
    }).start(wait = false)

    message{ "Стартанул" }.inlineKeyboardMarkup {
        "name" callback "cancel-5123"
    }.send(ORDER_CHAT_ID, bot)

    bot.handleUpdates()
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
