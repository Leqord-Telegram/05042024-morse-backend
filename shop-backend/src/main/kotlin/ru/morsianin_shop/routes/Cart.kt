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
import ru.morsianin_shop.model.CartItemRequest
import ru.morsianin_shop.resources.CartRequest
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredProduct
import ru.morsianin_shop.storage.StoredUser
import ru.morsianin_shop.storage.StoredUserCartItem
import ru.morsianin_shop.storage.StoredUserCartItems

fun Application.cartRoutes() {
    routing {
        authenticate("auth-jwt-user") {
            get<CartRequest> {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()

                val found = dbQuery {
                    StoredUserCartItem.find {
                        StoredUserCartItems.user eq userId
                    }.map { cartItem -> mapToResponse(cartItem) }
                }

                if (found.isNotEmpty()) {
                    call.respond(found)
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }
            }
            post<CartRequest> {
                val newCartItem = call.receive<CartItemRequest>()
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()

                dbQuery {
                    val productCandidate = StoredProduct.findById(newCartItem.productId)
                    val candidateUser = StoredUser.findById(userId)

                    if (productCandidate != null && candidateUser != null) {
                        val newStoredItem = StoredUserCartItem.new {
                            user = candidateUser
                            product = productCandidate
                            quantity = newCartItem.quantity
                        }
                        call.response.status(HttpStatusCode.Created)
                        call.respond(mapToResponse(newStoredItem))
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }
            get<CartRequest.Id> { id ->
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
                dbQuery {

                    val candidate = StoredUserCartItem.find {
                        StoredUserCartItems.id eq id.id
                        StoredUserCartItems.user eq userId
                    }.singleOrNull()

                    if (candidate != null) {
                        call.respond(candidate)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
            put<CartRequest.Id> { id ->
                val cartItem = call.receive<CartItemRequest>()
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
                dbQuery {
                    val candidate = StoredUserCartItem.find {
                        StoredUserCartItems.id eq id.id
                        StoredUserCartItems.user eq userId
                    }.singleOrNull()

                    val productCandidate = StoredProduct.findById(cartItem.productId)

                    if (candidate != null && productCandidate != null) {
                        candidate.quantity = cartItem.quantity
                        candidate.product = productCandidate
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
            delete<CartRequest.Id> { id ->
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
                dbQuery {
                    val candidate = StoredUserCartItem.find {
                        StoredUserCartItems.id eq id.id
                        StoredUserCartItems.user eq userId
                    }.singleOrNull()

                    if (candidate != null) {
                        candidate.delete()
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}