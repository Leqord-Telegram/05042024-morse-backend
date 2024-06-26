package ru.morsianin_shop.routes

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import ru.morsianin_shop.CANCEL_DURATION_KV_ID
import ru.morsianin_shop.resources.KVRequest

package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.ImageFormat
import ru.morsianin_shop.model.ResultCreated
import ru.morsianin_shop.model.UserPrivilege
import ru.morsianin_shop.plugins.hasPrivilege
import ru.morsianin_shop.resources.ImageRequest
import ru.morsianin_shop.storage.*
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery


fun Application.KVRoutes() {
    routing {
        get<KVRequest.cancelThreshold> {
            dbQuery {
                call.respond(StoredKV.findById(CANCEL_DURATION_KV_ID)?.value ?: "10")
            }
        }

        authenticate("auth-jwt-user") {
            put<KVRequest.cancelThreshold> {
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }

                dbQuery {
                    val candidate = StoredKV.findById(CANCEL_DURATION_KV_ID)
                    val newsusp =  call.receive<Long>().toString()

                    if (candidate != null) {
                        candidate.value = newsusp
                    }
                    else {
                        StoredKV.new(CANCEL_DURATION_KV_ID) {
                            value = newsusp
                        }
                    }
                }

            }
        }
    }
}