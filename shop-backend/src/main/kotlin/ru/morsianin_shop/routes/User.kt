package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.resources.UserRequest

fun Application.userRoutes() {
    routing {
        get<UserRequest> { users ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<UserRequest.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<UserRequest.Id.Cart> { cart ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        put<UserRequest.Id.Cart> { cart ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
    }
}