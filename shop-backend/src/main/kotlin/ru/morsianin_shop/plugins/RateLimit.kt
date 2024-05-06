package ru.morsianin_shop.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Application.configureRateLimit(searchRateLimit: Int? = null, refill: Duration = 60.seconds) {
    if (searchRateLimit != null) {
        install(RateLimit) {
            register(RateLimitName("search-suggestions")) {
                rateLimiter(limit = searchRateLimit, refillPeriod = refill)
            }
        }
        log.info("Rate limit initialized as $searchRateLimit for $refill")
    }
}