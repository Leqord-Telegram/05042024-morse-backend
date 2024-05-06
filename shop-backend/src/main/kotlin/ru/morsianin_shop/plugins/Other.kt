package ru.morsianin_shop.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*

fun Application.configureOther() {
    install(AutoHeadResponse)
    log.info("Auto HEAD responses initialized")
}