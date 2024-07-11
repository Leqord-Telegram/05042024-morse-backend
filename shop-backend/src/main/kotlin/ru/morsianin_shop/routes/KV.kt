package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.*
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

        get<KVRequest.phone> {
            dbQuery {
                call.respond(StoredKV.findById(PHONE_KV_ID)?.value ?: "")
            }
        }

        get<KVRequest.whatsapp> {
            dbQuery {
                call.respond(StoredKV.findById(WA_KV_ID)?.value ?: "")
            }
        }

        get<KVRequest.telegram> {
            dbQuery {
                call.respond(StoredKV.findById(TG_KV_ID)?.value ?: "")
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
                    val newsusp =  call.receiveText()

                    if (candidate != null) {
                        candidate.value = newsusp
                    }
                    else {
                        StoredKV.new(CANCEL_DURATION_KV_ID) {
                            value = newsusp
                        }
                    }

                    call.respond(HttpStatusCode.OK)
                }

            }

            put<KVRequest.aboutUs> {
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }

                dbQuery {
                    val candidate = StoredKV.findById(ABOUT_US_KV_ID)
                    val newsusp = call.receiveText()

                    if (candidate != null) {
                        candidate.value = newsusp
                    }
                    else {
                        StoredKV.new(ABOUT_US_KV_ID) {
                            value = newsusp
                        }
                    }

                    call.respond(HttpStatusCode.OK)
                }

            }

            put<KVRequest.phone> {
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }

                dbQuery {
                    val candidate = StoredKV.findById(PHONE_KV_ID)
                    val newsusp = call.receiveText()

                    if (candidate != null) {
                        candidate.value = newsusp
                    }
                    else {
                        StoredKV.new(PHONE_KV_ID) {
                            value = newsusp
                        }
                    }

                    call.respond(HttpStatusCode.OK)
                }

            }

            put<KVRequest.whatsapp> {
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }

                dbQuery {
                    val candidate = StoredKV.findById(WA_KV_ID)
                    val newsusp = call.receiveText()

                    if (candidate != null) {
                        candidate.value = newsusp
                    }
                    else {
                        StoredKV.new(WA_KV_ID) {
                            value = newsusp
                        }
                    }

                    call.respond(HttpStatusCode.OK)
                }

            }

            put<KVRequest.telegram> {
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }

                dbQuery {
                    val candidate = StoredKV.findById(TG_KV_ID)
                    val newsusp = call.receiveText()

                    if (candidate != null) {
                        candidate.value = newsusp
                    }
                    else {
                        StoredKV.new(TG_KV_ID) {
                            value = newsusp
                        }
                    }

                    call.respond(HttpStatusCode.OK)
                }

            }
        }
    }
}