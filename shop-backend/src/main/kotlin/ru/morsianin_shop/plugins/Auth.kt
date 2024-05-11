package ru.morsianin_shop.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*


object AuthSettings {
    private var secretKey: String? = null
    private var tgBotToken: String? = null

    fun getSecretKey(): String {
        if (secretKey != null) {
            return secretKey!!
        }
        else {
            val envSecret = System.getenv("JWT_SECRET")

            if (envSecret == null) {
                throw IllegalStateException("JWT_SECRET env variable is not set")
            }
            else {
                secretKey = System.getenv("JWT_SECRET")
                return secretKey!!
            }

        }
    }

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
}



fun Application.configureAuth() {
    install(Authentication) {
        jwt {

        }
    }
}