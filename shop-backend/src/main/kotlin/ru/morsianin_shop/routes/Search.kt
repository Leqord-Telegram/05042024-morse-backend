package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.resources.SearchRequest
import ru.morsianin_shop.search.SearchLevenshtein

fun Application.searchRoutes() {
    routing {
        rateLimit(RateLimitName("search")) {
            get<SearchRequest> { searchRequest ->
                if (searchRequest.query == null) {
                    call.respond(HttpStatusCode.BadRequest)
                }

                if (searchRequest.topN > 100) {
                    call.respond(HttpStatusCode.BadRequest)
                }

                val result = SearchLevenshtein.findTopBestMatchingProductsSuggestions(searchRequest.query!!, searchRequest.topN)

                call.respond(HttpStatusCode.OK, result)
            }
        }
    }
}