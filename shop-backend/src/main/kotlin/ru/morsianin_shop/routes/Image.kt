package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.mapping.Mapper.mapToDTO
import ru.morsianin_shop.resources.ImageRequest
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredImage

fun Application.imageRoutes() {
    routing {
        get<ImageRequest> { images->
            dbQuery {
                call.respond(StoredImage.all().map { img -> mapToDTO(img) })
            }
        }
        post<ImageRequest> { images->
            // TODO: implement
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        get<ImageRequest.Id> { id ->
            dbQuery {
                val candidate = StoredImage.all().singleOrNull()

                if (candidate != null) {
                    call.respond(mapToDTO(candidate))
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        put<ImageRequest.Id> { id ->
            // TODO: implement
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        delete<ImageRequest.Id> { id ->
            dbQuery {
                val candidate = StoredImage.findById(id.id)

                if (candidate != null) {
                    candidate.delete()
                    call.respond(HttpStatusCode.OK)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}