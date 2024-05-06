package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.resources.Products

fun Application.productRoutes() {
    routing {
        get<Products> { products ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        post<Products>  {
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<Products.Id> { id ->
            call.respondText("${id.id}", status = HttpStatusCode.NotImplemented)
        }
        put<Products.Id> { id ->
            call.respondText("${id.id}", status = HttpStatusCode.NotImplemented)
        }
        delete<Products.Id> { id ->
            call.respondText("${id.id}", status = HttpStatusCode.NotImplemented)
        }

    }
}