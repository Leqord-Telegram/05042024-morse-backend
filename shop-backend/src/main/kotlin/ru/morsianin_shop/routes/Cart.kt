package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
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

// TODO: авторизация и филтр по id пользователя
fun Application.cartRoutes() {
    routing {
        get<CartRequest> { filter ->
            val found = dbQuery {
                StoredUserCartItem.all().map { cartItem -> mapToResponse(cartItem) }
            }

            if (found.isNotEmpty()) {
                call.respond(found)
            }
            else {
                call.respond(HttpStatusCode.NoContent)
            }
        }
        post<CartRequest> {
            val newCartItem = call.receive<CartItemRequest>()

            dbQuery {
                val productCandidate = StoredProduct.findById(newCartItem.productId)

                if (productCandidate != null) {
                    val newStoredItem = StoredUserCartItem.new {
                        user = StoredUser.all().first() // TODO: УБРАТЬ!!
                        product = productCandidate
                        quantity = newCartItem.quantity
                    }
                    call.response.status(HttpStatusCode.Created)
                    call.respond(mapToResponse(newStoredItem))
                }
                else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
        get<CartRequest.Id> { id ->
            dbQuery {
                // TODO: НЕ ЗАБЫТЬ ПРОВЕРИТЬ ПОЛЬЗОВАТЕЛЯ И ТУТ
                val candidate = StoredUserCartItem.findById(id.id)

                if (candidate != null) {
                    call.respond(candidate)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        put<CartRequest.Id> { id ->
            val cartItem = call.receive<CartItemRequest>()

            dbQuery {
                val candidate = StoredUserCartItem.findById(id.id)

                val productCandidate = StoredProduct.findById(cartItem.productId)

                if (candidate != null && productCandidate != null) {
                    candidate.quantity = cartItem.quantity
                    candidate.product = productCandidate
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        delete<CartRequest.Id> { id ->
            dbQuery {
                val candidate = StoredUserCartItem.findById(id.id)

                if (candidate != null) {
                    candidate.delete()
                    call.respond(HttpStatusCode.NoContent)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}