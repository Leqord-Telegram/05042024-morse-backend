package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.resources.post
import io.ktor.server.routing.routing
import org.apache.commons.codec.digest.HmacUtils
import ru.morsianin_shop.model.AuthType
import ru.morsianin_shop.plugins.AuthSettings
import ru.morsianin_shop.resources.AuthRequest
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


fun Application.authRoutes() {
    routing {
        post<AuthRequest> { request ->

            if (request.type != AuthType.Telegram) {
                call.respondText("Unknown type ${request.type}", status = HttpStatusCode.BadRequest)
                return@post
            }

            val initDataRaw = call.receive<String>()
            val initData = parseQueryString(initDataRaw)
            val receivedHash = initData["hash"]

            if (receivedHash == null) {
                call.respondText("missing hash", status = HttpStatusCode.BadRequest)
                return@post
            }

            val botToken = AuthSettings.getTgBotToken()
            val calculatedHash = hmacSha256(buildDataCheckString(initDataRaw), generateSecretKey(botToken))

            if (receivedHash != calculatedHash) {
                call.respond(HttpStatusCode.Unauthorized, "Fucked up, bitch: hashes doesn't match")
                return@post
            }

            call.respond(HttpStatusCode.OK, "Validation successful")
        }
    }
}

private fun generateSecretKey(botToken: String): ByteArray {
    return HmacUtils("HmacSHA256", "WebAppData").hmac(botToken)!!
}

private fun hmacSha256(data: String, key: ByteArray): String {
    val hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key, "HmacSHA256")
    hmac.init(secretKey)
    val bytes = hmac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}

private fun buildDataCheckString(initData: String): String {
    var queryParams = initData.split("&").associate {
        val (key, value) = it.split("=")
        key to value
    }

    queryParams = queryParams.filterNot { (key, _) -> key == "hash" }

    val sortedKeys = queryParams.keys.sorted()
    return sortedKeys.joinToString("\n") { key -> "$key=${queryParams[key]}" }
}