package ru.morsianin_shop.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import ru.morsianin_shop.plugins.AuthSettings.getJwtSettingsUserspace
import java.util.*

data class AuthJWTSettings(
    val secretKey: String,
    val audience: String,
    val realm: String,
    val issuer: String,
)

object AuthSettings {
    private var tgBotToken: String? = null
    private var jwtSettingsUserspace: AuthJWTSettings? = null

    fun getTgBotToken(): String {
        if (tgBotToken != null) {
            return tgBotToken!!
        }
        else {
            val tgBotTokenEnv = System.getenv("TG_BOT_TOKEN")

            if (tgBotTokenEnv == null) {
                throw IllegalStateException("TG_BOT_TOKEN env variable is not set")
            }
            else {
                tgBotToken = tgBotTokenEnv
                return tgBotToken!!
            }
        }
    }

    fun getJwtSettingsUserspace(): AuthJWTSettings {
        if (jwtSettingsUserspace == null) {
            jwtSettingsUserspace = AuthJWTSettings(
                secretKey = System.getenv("JWT_SECRET"),
                audience = System.getenv("JWT_AUDIENCE"),
                issuer = System.getenv("JWT_ISSUER"),
                realm = "userspace"
            )
            return jwtSettingsUserspace!!
        }
        else {
            return jwtSettingsUserspace!!
        }
    }
}



fun Application.configureAuth() {
    install(Authentication) {
        jwt("auth-jwt-user") {
            realm = getJwtSettingsUserspace().realm

            verifier(
                JWT
                .require(Algorithm.HMAC256(getJwtSettingsUserspace().secretKey))
                .withAudience(getJwtSettingsUserspace().audience)
                .withIssuer(getJwtSettingsUserspace().issuer)
                .build())

            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token for $realm is not valid or has expired")
            }
        }
    }
}