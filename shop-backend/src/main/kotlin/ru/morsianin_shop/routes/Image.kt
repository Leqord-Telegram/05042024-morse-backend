package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.resources.Images

fun Application.imageRoutes() {
    routing {
        get<Images> { images->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        post<Images> { images->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<Images.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        put<Images.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        delete<Images.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
    }
}