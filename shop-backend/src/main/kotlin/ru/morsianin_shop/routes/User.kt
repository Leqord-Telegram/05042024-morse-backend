package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.resources.Users

fun Application.userRoutes() {
    routing {
        get<Users> { users ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<Users.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<Users.Id.Cart> { cart ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        put<Users.Id.Cart> { cart ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
    }
}