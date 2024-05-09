package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.resources.OrderRequest

fun Application.orderRoutes() {
    routing {
        get<OrderRequest> { orders ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        post<OrderRequest> { orders ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<OrderRequest.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        put<OrderRequest.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        delete<OrderRequest.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
    }
}