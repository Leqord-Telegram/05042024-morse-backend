package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.TransactionManager
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.OrderItemChanged
import ru.morsianin_shop.model.OrderItemNew
import ru.morsianin_shop.model.OrderNew
import ru.morsianin_shop.model.OrderStatus
import ru.morsianin_shop.resources.OrderRequest
import ru.morsianin_shop.storage.*
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredOrderItems.order
import ru.morsianin_shop.storage.StoredOrders.status

// TODO: авторизация
fun Application.orderRoutes() {
    routing {
        get<OrderRequest> { filter ->
            var query: Op<Boolean> = Op.TRUE

            filter.status?.let {
                query = query and (StoredOrders.status eq it)
            }

            val response = dbQuery {
                StoredOrder.find { query }.map { order -> mapToResponse(order) }
            }

            if(response.isNotEmpty()) {
                call.respond(response)
            }
            else {
                call.respond(HttpStatusCode.NoContent)
            }
        }
        post<OrderRequest> { orders ->
            upsertOrder()
        }
        get<OrderRequest.Id> { id ->
             dbQuery {
                val candidate = StoredOrder.findById(id.id)

                if (candidate != null) {
                    call.respond(mapToResponse(candidate))
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }


        }
        delete<OrderRequest.Id> { id ->
            dbQuery {
                val candidate = StoredOrder.findById(id.id)

                if (candidate != null) {
                    candidate.delete()
                    call.respond(HttpStatusCode.NoContent)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        get<OrderRequest.Id.Status> { status ->
            dbQuery {
                val candidate = StoredOrder.findById(status.parent.id)

                if (candidate != null) {
                    call.respond(candidate.status)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        put<OrderRequest.Id.Status> { status ->
            val newStatus = call.receive<OrderStatus>()
            dbQuery {
                val candidate = StoredOrder.findById(status.parent.id)

                if (candidate != null) {
                    candidate.status = newStatus
                    call.respond(HttpStatusCode.OK)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        get<OrderRequest.Id.Item> { item ->
            val candidate = dbQuery {
                StoredOrder.findById(item.parent.id)
            }

            if (candidate != null) {
                call.respond(candidate.items.map { mapToResponse(it) })
            }
            else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post<OrderRequest.Id.Item> { item ->
            val newItem = call.receive<OrderItemNew>()
            dbQuery {
                val candidateOrder = StoredOrder.findById(item.parent.id)
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

            dbQuery {
                val candidateItem = StoredOrderItem.findById(item.itemId)

                if (candidateItem != null) {
                    candidateItem.quantity = changedItem.quantity
                    call.respond(HttpStatusCode.OK)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        delete<OrderRequest.Id.Item.ItemId> { itemId ->
            dbQuery {
                val candidateItem = StoredOrderItem.findById(itemId.itemId)

                if (candidateItem != null) {
                    candidateItem.delete()
                    call.respond(HttpStatusCode.NoContent)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.upsertOrder() {
    val newOrder = call.receive<OrderNew>()

    dbQuery {
        val userCandidate = StoredUser.all().first()// TODO: ИЗМЕНИТЬ

        val currentOrder: StoredOrder  = StoredOrder.new {
                user = userCandidate
                status = newOrder.status // TODO: проконтролировать возможные статусы после авторизации
            }

        var productCandidate: StoredProduct? = null
        for (item in newOrder.items) {
            productCandidate = StoredProduct.findById(item.productId)

            if (productCandidate != null) {
                StoredOrderItem.new {
                    order = currentOrder
                    product = productCandidate
                    quantity = item.quantity
                }
            } else {
                TransactionManager.current().rollback()
                call.respond(HttpStatusCode.BadRequest)
                return@dbQuery
            }
        }

        call.respond(HttpStatusCode.Created)
    }
}