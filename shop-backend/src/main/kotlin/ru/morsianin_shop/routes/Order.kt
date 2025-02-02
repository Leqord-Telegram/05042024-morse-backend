package ru.morsianin_shop.routes

import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.ParseMode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.TransactionManager
import ru.morsianin_shop.CANCEL_DURATION_KV_ID
import ru.morsianin_shop.ORDER_CHAT_ID
import ru.morsianin_shop.bot
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.OrderNew
import ru.morsianin_shop.model.OrderStatus
import ru.morsianin_shop.model.printOrderMessage
import ru.morsianin_shop.resources.OrderRequest
import ru.morsianin_shop.storage.*
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredOrderItems.order
import java.time.LocalDateTime

// TODO: авторизация
fun Application.orderRoutes() {

    routing {
        authenticate("auth-jwt-user") {
            get<OrderRequest> { filter ->
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()

                var query: Op<Boolean> = Op.TRUE

                query = query and (StoredOrders.user eq EntityID(userId, StoredUsers))

                filter.status?.let {
                    query = query and (StoredOrders.status eq it)
                }

                val response = dbQuery {
                    StoredOrder.find { query }.map { order -> mapToResponse(order) }
                }

                if (response.isNotEmpty()) {
                    call.respond(response)
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }
            }

        post<OrderRequest> {
            upsertOrder()
        }
        get<OrderRequest.Id> { id ->
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
             dbQuery {
                val candidate = StoredOrder.find {
                    (StoredOrders.id eq EntityID(id.id, StoredOrders)) and (StoredOrders.user eq userId)
                }.singleOrNull()

                if (candidate != null) {
                    call.respond(mapToResponse(candidate))
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }


        }
        /*
        delete<OrderRequest.Id> { id ->
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
            dbQuery {
                val candidate = StoredOrder.find {
                    StoredOrders.id eq EntityID(id.id, StoredOrders)
                    StoredOrders.user eq userId
                }.singleOrNull()

                if (candidate != null) {
                    candidate.delete()
                    call.respond(HttpStatusCode.NoContent)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        */
        get<OrderRequest.Id.Status> { status ->
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
            dbQuery {
                val candidate = StoredOrder.find {
                    (StoredOrders.id eq EntityID(status.parent.id, StoredOrders)) and (StoredOrders.user eq userId)
                }.singleOrNull()

                if (candidate != null) {
                    call.respond(candidate.status)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        /*
        put<OrderRequest.Id.Status> { status ->
            val newStatus = call.receive<OrderStatus>()
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
            dbQuery {
                val candidate = StoredOrder.find {
                    StoredOrders.id eq EntityID(status.parent.id, StoredOrders)
                    StoredOrders.user eq userId
                }.singleOrNull()

                if (candidate != null) {
                    candidate.status = newStatus
                    call.respond(HttpStatusCode.OK)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    */
        post<OrderRequest.Id.Cancel> { cancel ->
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()

            dbQuery {
                val candidate = StoredOrder.find {
                    (StoredOrders.id eq EntityID(cancel.parent.id, StoredOrders)) and (StoredOrders.user eq userId)
                }.singleOrNull()

                //TODO: учитывает UTC+0, так себе решение
                val cancelThreshold = (StoredKV.findById(CANCEL_DURATION_KV_ID)?.value?.toLong() ?: 10)

                if (candidate != null) {
                    if (candidate.shipmentDateTime?.minusHours(cancelThreshold)?.isAfter(LocalDateTime.now().plusHours(10)) ?: true) {
                        candidate.status = OrderStatus.CANCELED

                        for (itemr in candidate.items) {
                            itemr.product.quantity += itemr.quantity
                        }

                        message{ "Пользователь отменил заказ ${mapToResponse(candidate).shittyId}" }.inlineKeyboardMarkup {
                        }.send(ORDER_CHAT_ID, bot)

                        call.respond(mapToResponse(candidate))
                    }
                    else {
                        call.respond(HttpStatusCode.BadRequest, "Too late")
                    }

                }
                else {
                    call.respond(HttpStatusCode.NotFound, "There is no ${cancel.parent.id} for ${userId}")
                }
            }
        }

        get<OrderRequest.Id.Item> { item ->
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
            dbQuery {
                val candidate = StoredOrder.find {
                    (StoredOrders.id eq EntityID(item.parent.id, StoredOrders)) and (StoredOrders.user eq userId)
                }.singleOrNull()

                if (candidate != null) {
                    if (candidate.items.toList().isEmpty()) {
                        call.respond(HttpStatusCode.NoContent)
                    }
                    else {
                        call.respond(candidate.items.map { mapToResponse(it) })
                    }
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
            /*
        post<OrderRequest.Id.Item> { item ->
            val newItem = call.receive<OrderItemNew>()
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
            dbQuery {
                val candidateOrder = StoredOrder.find {
                    StoredOrders.id eq EntityID(item.parent.id, StoredOrders)
                    StoredOrders.user eq userId
                }.singleOrNull()
                val candidateProduct = StoredProduct.findById(newItem.productId)

                if (candidateOrder != null && candidateProduct != null) {
                    val newStoredOrderItem = StoredOrderItem.new {
                        order = candidateOrder
                        product = candidateProduct
                        quantity = newItem.quantity
                    }
                    call.response.status(HttpStatusCode.Created)
                    call.respond(mapToResponse(newStoredOrderItem))
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        put<OrderRequest.Id.Item.ItemId> { item ->
            val changedItem = call.receive<OrderItemChanged>()
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
            dbQuery {
                val candidateOrder = StoredOrder.find {
                    StoredOrders.id eq EntityID(item.parent.id, StoredOrders)
                    StoredOrders.user eq userId
                }.singleOrNull()

                if (candidateOrder == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@dbQuery
                }

                val candidateItem = StoredOrderItem.find {
                    StoredOrderItems.id eq EntityID(item.parent.id, StoredOrderItems)
                    StoredOrderItems.order eq candidateOrder.id
                }.singleOrNull()

                if (candidateItem != null) {
                    candidateItem.quantity = changedItem.quantity
                    call.respond(HttpStatusCode.OK)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        delete<OrderRequest.Id.Item.ItemId> { item ->
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()
            dbQuery {
                val candidateOrder = StoredOrder.find {
                    StoredOrders.id eq EntityID(item.parent.id, StoredOrders)
                    StoredOrders.user eq userId
                }.singleOrNull()

                if (candidateOrder == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@dbQuery
                }

                val candidateItem = StoredOrderItem.find {
                    StoredOrderItems.id eq EntityID(item.parent.id, StoredOrderItems)
                    StoredOrderItems.order eq candidateOrder.id
                }.singleOrNull()

                if (candidateItem != null) {
                    candidateItem.delete()
                    call.respond(HttpStatusCode.NoContent)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        */
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.upsertOrder() {
    val newOrder = call.receive<OrderNew>()
    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()

    dbQuery {
        val userCandidate = StoredUser.findById(userId)

        if (userCandidate == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@dbQuery
        }



        val currentOrder: StoredOrder  = StoredOrder.new {
            user = userCandidate
            status = OrderStatus.PENDING
            shipment = newOrder.shipment
            shipmentDateTime = newOrder.shipmentDateTime
            shipmentAddress = newOrder.shipmentAddress
            description = newOrder.description
            userName = newOrder.userName
            phone = newOrder.phone
        }

        var productCandidate: StoredProduct?
        for (item in newOrder.items) {
            productCandidate = StoredProduct.findById(item.productId)

            if (productCandidate != null && productCandidate.quantity > 0 && productCandidate.enabled) {
                StoredOrderItem.new {
                    order = currentOrder
                    product = productCandidate!!
                    quantity = item.quantity
                }

                productCandidate.quantity -= item.quantity
            } else {
                TransactionManager.current().rollback()
                call.respond(HttpStatusCode.BadRequest)
                return@dbQuery
            }
        }
        val mappedOrder = mapToResponse(currentOrder)

        message{ printOrderMessage(mappedOrder, currentOrder.user.tgId, currentOrder.user.name) }.inlineKeyboardMarkup {
            "❌" callback "cancel${currentOrder.id.value}"
            "✓" callback "shipped${currentOrder.id.value}"
        }.options {
            parseMode = ParseMode.Markdown
        }.send(ORDER_CHAT_ID, bot)


        message {
                """
                |Дополнительно для заказа ${mappedOrder.shittyId}
                |Контакты: ${if (currentOrder.user.name != null) "@${currentOrder.user.name}" else (if (currentOrder.user.tgId != null) "[Tg](tg://user?id=${currentOrder.user.tgId})" else "НЕ УКАЗАНЫ")}
            """.trimMargin()
        }.options {
                parseMode = ParseMode.Markdown
        }.send(ORDER_CHAT_ID, bot)

        call.respond(HttpStatusCode.Created)

    }
}