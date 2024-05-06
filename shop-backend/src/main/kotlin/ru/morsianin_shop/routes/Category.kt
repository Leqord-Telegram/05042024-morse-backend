package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.resources.Categories

fun Application.categoryRoutes() {
    routing {
        get<Categories> { categories ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        post<Categories> { category ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<Categories.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        put<Categories.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        delete<Categories.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
    }
}