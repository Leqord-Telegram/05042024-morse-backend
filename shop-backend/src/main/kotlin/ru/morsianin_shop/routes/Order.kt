package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.resources.Orders

fun Application.orderRoutes() {
    routing {
        get<Orders> { orders ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        post<Orders> { orders ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<Orders.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        put<Orders.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        delete<Orders.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
    }
}