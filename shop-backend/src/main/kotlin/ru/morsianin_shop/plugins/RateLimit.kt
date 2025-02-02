package ru.morsianin_shop.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureRateLimit() {
    val refill = 60.seconds
    val searchRateLimit = System.getenv("RATE_SEARCH")?.toIntOrNull()?: 3600


    install(RateLimit) {
        register(RateLimitName("search")) {
            rateLimiter(limit = searchRateLimit, refillPeriod = refill)
        }
    }
    log.info("Rate limit initialized as $searchRateLimit for $refill")

}