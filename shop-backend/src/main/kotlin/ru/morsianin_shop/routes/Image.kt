package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.UserPrivilege
import ru.morsianin_shop.plugins.hasPrivilege
import ru.morsianin_shop.resources.ImageRequest
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredImage

fun Application.imageRoutes() {
    routing {
        get<ImageRequest> { images->
            dbQuery {
                call.respond(StoredImage.all().map { img -> mapToResponse(img) })
            }
        }
        authenticate("auth-jwt-user") {
            post<ImageRequest> { images ->
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }
                // TODO: implement
                call.respondText("tbi", status = HttpStatusCode.NotImplemented)
            }
        }
        get<ImageRequest.Id> { id ->
            dbQuery {
                val candidate = StoredImage.all().singleOrNull()

                if (candidate != null) {
                    call.respond(mapToResponse(candidate))
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        authenticate("auth-jwt-user") {
            put<ImageRequest.Id> { id ->
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }
                // TODO: implement
                call.respondText("tbi", status = HttpStatusCode.NotImplemented)
            }
            delete<ImageRequest.Id> { id ->
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@delete
                }

                dbQuery {
                    val candidate = StoredImage.findById(id.id)

                    if (candidate != null) {
                        candidate.delete()
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}