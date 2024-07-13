package ru.morsianin_shop

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.CommonHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.api.answer.answerCallbackQuery
import eu.vendeli.tgbot.api.chat.getChat
import eu.vendeli.tgbot.api.message.editMessageText
import eu.vendeli.tgbot.api.message.editText
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.api.poll
import eu.vendeli.tgbot.types.ParseMode
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.chat.Chat
import eu.vendeli.tgbot.types.internal.CallbackQueryUpdate
import eu.vendeli.tgbot.types.internal.EditedMessageUpdate
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import eu.vendeli.tgbot.types.internal.UpdateType
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.statements.StatementType
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.OrderShipment
import ru.morsianin_shop.model.OrderStatus
import ru.morsianin_shop.model.printOrderMessage
import ru.morsianin_shop.plugins.*
import ru.morsianin_shop.routes.*
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredOrder
import ru.morsianin_shop.storage.StoredOrderItems.order
import ru.morsianin_shop.storage.StoredOrders
import ru.morsianin_shop.storage.configureStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

val CANCEL_DURATION_KV_ID: Long = 4123;
val ORDER_CHAT_ID: Long = -1002172780422;
val ABOUT_US_KV_ID: Long = 5163;
val WA_KV_ID: Long = 71562;
val TG_KV_ID: Long = 82234;
val PHONE_KV_ID: Long = 93245;

val bot = TelegramBot(System.getenv("TG_BOT_TOKEN"))



@CommandHandler(["/start"])
suspend fun start(user: User, bot: TelegramBot) {
    message { "Привет!" }.send(user, bot)
    message { "Нажми кнопку 'Магазин', чтобы открыть \uD83D\uDC47 приложение" }.send(user, bot)
}

@CommonHandler.Regex("^cancel.*$", scope = [UpdateType.CALLBACK_QUERY])
suspend fun cancelOrder(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
    val id = update.callbackQuery.data!!.removePrefix("cancel").toLong()

    val order = dbQuery {
        StoredOrder.findById(id)?.let { mapToResponse(it) }
    }

    if (order != null) {
        editText(update.callbackQuery.message!!.messageId) {
            "Отменить заказ ${order.shittyId}?"
        }.inlineKeyboardMarkup {
            "Да" callback "approvedcancel${id}"
            "Нет" callback "declinedcancel${id}"
        }.options {
            parseMode = ParseMode.Markdown
        }.send(ORDER_CHAT_ID, bot)
    }
}


@CommonHandler.Regex("^declinedcancel.*$", scope = [UpdateType.CALLBACK_QUERY])
suspend fun declinedCancelOrder(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
    val id = update.callbackQuery.data!!.removePrefix("declinedcancel").toLong()

   dbQuery {
       val order = StoredOrder.findById(id)

        if (order != null) {
            editText(update.callbackQuery.message!!.messageId) {
                printOrderMessage(mapToResponse(order), order.user.tgId, order.user.name)
            }.inlineKeyboardMarkup {
                "❌" callback "cancel${id}"
                "✓" callback "shipped${id}"
            }.options {
                parseMode = ParseMode.Markdown
            }.send(ORDER_CHAT_ID, ru.morsianin_shop.bot)
        }
        else {
            message { "Ошибка" }.options {
                parseMode = ParseMode.Markdown
            }.send(ORDER_CHAT_ID, bot)
        }
    }


}

@CommonHandler.Regex("^approvedcancel.*$", scope = [UpdateType.CALLBACK_QUERY])
suspend fun approvedCancelOrder(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
    val id = update.callbackQuery.data!!.removePrefix("approvedcancel").toLong()

    dbQuery {
        val order = StoredOrder.findById(id)

        if (order != null) {
            order.status = OrderStatus.CANCELED

            for (itemr in order.items) {
                itemr.product.quantity += itemr.quantity
            }

            editText(update.callbackQuery.message!!.messageId) {
                "Заказ ${mapToResponse(order).shittyId} #отменён"
            }.options {
                parseMode = ParseMode.Markdown
            }.send(ORDER_CHAT_ID, bot)
        }
        else {
            message { "Ошибка" }.options {
                parseMode = ParseMode.Markdown
            }.send(ORDER_CHAT_ID, bot)
        }
    }
}

@CommonHandler.Regex("^shipped.*$", scope = [UpdateType.CALLBACK_QUERY])
suspend fun shippedOrder(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
    val id = update.callbackQuery.data!!.removePrefix("shipped").toLong()

    val order = dbQuery {
        StoredOrder.findById(id)?.let { mapToResponse(it) }
    }

    if (order != null) {
        editText(update.callbackQuery.message!!.messageId) {
            "Выдать заказ ${order.shittyId}?"
        }.inlineKeyboardMarkup {
            "Да" callback "approvedshipped${id}"
            "Нет" callback "declinedshipped${id}"
        }.options {
            parseMode = ParseMode.Markdown
        }.send(ORDER_CHAT_ID, bot)
    }
}


@CommonHandler.Regex("^declinedshipped.*$", scope = [UpdateType.CALLBACK_QUERY])
suspend fun declinedShippedOrder(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
    val id = update.callbackQuery.data!!.removePrefix("declinedshipped").toLong()

    dbQuery {
        val order = StoredOrder.findById(id)

        if (order != null) {

            editText(update.callbackQuery.message!!.messageId) {
                printOrderMessage(mapToResponse(order), order.user.tgId, order.user.name)
            }.inlineKeyboardMarkup {
                "❌" callback "cancel${id}"
                "✓" callback "shipped${id}"
            }.options {
                parseMode = ParseMode.Markdown
            }.send(ORDER_CHAT_ID, ru.morsianin_shop.bot)
        }
        else {
            message { "Ошибка" }.send(ORDER_CHAT_ID, bot)
        }
    }
}

@CommonHandler.Regex("^approvedshipped.*$", scope = [UpdateType.CALLBACK_QUERY])
suspend fun approvedShippedOrder(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
    val id = update.callbackQuery.data!!.removePrefix("approvedshipped").toLong()

    dbQuery {
        val order = StoredOrder.findById(id)

        if (order != null) {
            order.status = OrderStatus.FINISHED

            editText(update.callbackQuery.message!!.messageId) {
                "Заказ ${mapToResponse(order).shittyId} #выдан"
            }.options {
                parseMode = ParseMode.Markdown
            }.send(ORDER_CHAT_ID, bot)
        }
        else {
            message { "Ошибка" }.send(ORDER_CHAT_ID, bot)
        }
    }
}


fun main() {
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

    runBlocking {
        message { "*Бот запущен*" }.options {
            parseMode = ParseMode.Markdown
        }.send(ORDER_CHAT_ID, bot)

        launch(Dispatchers.Unconfined) {
            for (e in bot.update.caughtExceptions) {
                message { "Ошибка: $e" }.send(ORDER_CHAT_ID, bot)
                delay(100)
            }
        }

        bot.handleUpdates()
    }
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
