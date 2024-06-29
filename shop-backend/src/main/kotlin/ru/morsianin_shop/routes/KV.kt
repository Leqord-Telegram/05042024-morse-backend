package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.ABOUT_US_KV_ID
import ru.morsianin_shop.CANCEL_DURATION_KV_ID
import ru.morsianin_shop.model.UserPrivilege
import ru.morsianin_shop.plugins.hasPrivilege
import ru.morsianin_shop.resources.KVRequest
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredKV


fun Application.KVRoutes() {
    routing {
        get<KVRequest.cancelThreshold> {
            dbQuery {
                call.respond(StoredKV.findById(CANCEL_DURATION_KV_ID)?.value ?: "10")
            }
        }

        get<KVRequest.aboutUs> {
            dbQuery {
                call.respond(StoredKV.findById(ABOUT_US_KV_ID)?.value ?: "")
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

            put<KVRequest.aboutUs> {
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }

                dbQuery {
                    val candidate = StoredKV.findById(ABOUT_US_KV_ID)
                    val newsusp = call.receive<String>()

                    if (candidate != null) {
                        candidate.value = newsusp
                    }
                    else {
                        StoredKV.new(ABOUT_US_KV_ID) {
                            value = newsusp
                        }
                    }
                }

            }
        }
    }
}