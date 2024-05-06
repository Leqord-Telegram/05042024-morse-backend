package ru.morsianin_shop.plugins

import io.ktor.server.application.*
import io.ktor.server.resources.*

fun Application.configureResources() {
    install(Resources)
    log.info("Resources initialized")
}