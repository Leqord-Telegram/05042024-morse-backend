package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.LabelNew
import ru.morsianin_shop.model.UserPrivilege
import ru.morsianin_shop.plugins.hasPrivilege
import ru.morsianin_shop.resources.LabelRequest
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredCategory
import ru.morsianin_shop.storage.StoredLabel


fun Application.labelRoutes() {
    routing {
        get<LabelRequest> {
            val response = dbQuery {
                StoredLabel.all().map { mapToResponse(it) }
            }

            if(response.isNotEmpty()) {
                call.respond(response)
            }
            else {
                call.respond(HttpStatusCode.NotFound)
            }

        }
        get<LabelRequest.Id> { id ->
            val response = dbQuery {
                StoredCategory.findById(id.id)
            }

            call.respond(response?.let { mapToResponse(it) } ?: HttpStatusCode.NotFound)
        }

        authenticate("auth-jwt-user") {
            post<LabelRequest> {
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                val newLabel = call.receive<LabelNew>()
                val newStoredLabel = dbQuery {
                    StoredLabel.new {
                        name = newLabel.name
                        colorRed = newLabel.color.red.toShort()
                        colorGreen = newLabel.color.green.toShort()
                        colorBlue = newLabel.color.blue.toShort()
                    }
                }
                call.response.status(HttpStatusCode.Created)
                call.respond(mapToResponse(newStoredLabel))
            }
            put<LabelRequest.Id> { id ->
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }

                val newLabel = call.receive<LabelNew>()

                dbQuery {
                    val candidate = StoredLabel.findById(id.id)

                    if (candidate != null) {
                        candidate.name = newLabel.name
                        candidate.colorRed = newLabel.color.red.toShort()
                        candidate.colorGreen = newLabel.color.green.toShort()
                        candidate.colorBlue = newLabel.color.blue.toShort()

                        call.respond(HttpStatusCode.OK)
                    } else {
                        val createdLabel = StoredLabel.new {
                            name = newLabel.name
                            colorRed = newLabel.color.red.toShort()
                            colorGreen = newLabel.color.green.toShort()
                            colorBlue = newLabel.color.blue.toShort()
                        }

                        call.response.status(HttpStatusCode.Created)
                        call.respond(mapToResponse(createdLabel))
                    }
                }
            }
            delete<LabelRequest.Id> { id ->
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@delete
                }

                dbQuery {
                    val candidate = StoredLabel.findById(id.id)
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