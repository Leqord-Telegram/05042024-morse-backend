package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.resources.ImageRequest

fun Application.imageRoutes() {
    routing {
        get<ImageRequest> { images->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        post<ImageRequest> { images->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<ImageRequest.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        put<ImageRequest.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        delete<ImageRequest.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
    }
}