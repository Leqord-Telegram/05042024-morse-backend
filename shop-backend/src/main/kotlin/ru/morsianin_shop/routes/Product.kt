package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.resources.ProductRequest
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery

fun Application.productRoutes() {
    routing {
        get<ProductRequest> { products ->
            dbQuery {
                
            }
        }
        post<ProductRequest>  {
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<ProductRequest.Id> { id ->
            call.respondText("${id.id}", status = HttpStatusCode.NotImplemented)
        }
        put<ProductRequest.Id> { id ->
            call.respondText("${id.id}", status = HttpStatusCode.NotImplemented)
        }
        delete<ProductRequest.Id> { id ->
            call.respondText("${id.id}", status = HttpStatusCode.NotImplemented)
        }

    }
}