package ru.morsianin_shop.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.resources.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import org.apache.commons.codec.digest.HmacUtils
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.AuthType
import ru.morsianin_shop.model.TelegramAuthUserData
import ru.morsianin_shop.model.UserResponse
import ru.morsianin_shop.plugins.AuthSettings
import ru.morsianin_shop.plugins.AuthSettings.getJwtSettingsUserspace
import ru.morsianin_shop.resources.AuthRequest
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredUser
import ru.morsianin_shop.storage.StoredUsers
import ru.morsianin_shop.storage.StoredUsers.tgId
import java.nio.charset.StandardCharsets
import java.security.InvalidParameterException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.time.Duration.Companion.hours


fun Application.authRoutes() {
    routing {
        post<AuthRequest> { request ->

            if (request.type != AuthType.Telegram) {
                call.respondText("Unknown login method ${request.type}", status = HttpStatusCode.BadRequest)
                return@post
            }

            val initDataRaw = call.receive<String>()
            val initData = parseQueryString(initDataRaw)
            val receivedHash = initData["hash"]?: throw InvalidParameterException("Hash is missing")

            val botToken = AuthSettings.getTgBotToken()
            val calculatedHash = hmacSha256(buildDataCheckString(initDataRaw), generateSecretKey(botToken))

            if (receivedHash != calculatedHash) {
                call.respond(HttpStatusCode.Unauthorized, "Fucked up, bitch: hashes doesn't match")
                return@post
            }

            val userJson = initData["user"]?: throw InvalidParameterException("User data is missing")

            val json = Json { ignoreUnknownKeys = true }

            val tgUserData = json.decodeFromString<TelegramAuthUserData>(userJson)

            var user: UserResponse? = null
            dbQuery {
                val candidate = StoredUser.find {
                    tgId eq tgUserData.id
                }.map { it }

                if (candidate.size == 1) {
                    user = mapToResponse(candidate.first())
                    return@dbQuery
                }
                else if (candidate.isEmpty()) {
                    user = mapToResponse(StoredUser.new {
                        name = tgUserData.username
                        tgId = tgUserData.id
                    })
                    return@dbQuery
                } else {
                    throw InvalidParameterException("User data is more than one candidate")
                }
            }

            val token = JWT.create()
                .withAudience(getJwtSettingsUserspace().audience)
                .withIssuer(getJwtSettingsUserspace().issuer)
                .withClaim("user-id", user!!.id)
                .withExpiresAt(Date(System.currentTimeMillis() + 48.hours.inWholeMilliseconds))
                .sign(Algorithm.HMAC256(getJwtSettingsUserspace().secretKey))

            call.respond(token)
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