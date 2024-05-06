package ru.morsianin_shop.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureOther() {
    install(AutoHeadResponse)
    log.info("Auto HEAD responses initialized")
}