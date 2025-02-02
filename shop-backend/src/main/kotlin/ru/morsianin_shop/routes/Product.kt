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
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import ru.morsianin_shop.NOTIFY_CHAT_ID
import ru.morsianin_shop.ORDER_CHAT_ID
import ru.morsianin_shop.bot
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.*
import ru.morsianin_shop.plugins.hasPrivilege
import ru.morsianin_shop.resources.ProductRequest
import ru.morsianin_shop.storage.*
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredProductCategories.category
import java.time.LocalDate

fun Application.productRoutes() {
    routing {
        get<ProductRequest> { filter ->
            var query: Op<Boolean> = Op.TRUE

            query = query and (StoredProducts.enabled eq true)

            filter.name?.let {
                query = query and (StoredProducts.name eq it)
            }

            filter.description?.let {
                query = query and (StoredProducts.description eq it)
            }

            filter.price?.let {
                query = query and (StoredProducts.price eq it)
            }

            filter.quantity?.let {
                query = query and (StoredProducts.quantity eq it)
            }

            filter.active?.let {
                query = query and (StoredProducts.active eq it)
            }

            val sortType = when (filter.sort) {
                ProductSort.PriceAsc -> StoredProducts.price to SortOrder.ASC
                ProductSort.PriceDesc -> StoredProducts.price to SortOrder.DESC
                ProductSort.NameAsc -> StoredProducts.name to SortOrder.ASC
                ProductSort.NameDesc -> StoredProducts.name to SortOrder.DESC
                ProductSort.QuantityAsc -> StoredProducts.quantity to SortOrder.ASC
                ProductSort.QuantityDesc -> StoredProducts.quantity to SortOrder.DESC
                ProductSort.CreatedAsc -> StoredProducts.createdAt to SortOrder.ASC
                ProductSort.CreatedDesc -> StoredProducts.createdAt to SortOrder.DESC
                ProductSort.IdAsc -> StoredProducts.id to SortOrder.ASC
                ProductSort.PriorityAsc -> if (filter.categoryId != null) {
                    StoredProductCategories.priority to SortOrder.ASC
                }
                else {
                    StoredProducts.id to SortOrder.ASC // TODO: пока нет отдельной категории для всех, будет так
                }
            }

            val found =
                if(filter.categoryId != null) {
                    dbQuery {
                        StoredProduct.wrapRows(
                            StoredProducts.leftJoin(StoredProductCategories).select(StoredProducts.columns).where {
                                query and (category eq filter.categoryId)
                            }.orderBy(sortType)
                                .limit(filter.limit, filter.offset)
                        ).toList().map { mapToResponse(it) }
                    }
                }
            else {
                    dbQuery {
                        StoredProduct.wrapRows(
                            StoredProducts.select(StoredProducts.columns).where {
                                query
                            }.orderBy(sortType)
                                .limit(filter.limit, filter.offset)
                        ).toList().map { mapToResponse(it) }
                    }
                }

            if (found.isNotEmpty()) {
                call.respond(found)
            }
            else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        get<ProductRequest.Total> {
            val products = dbQuery {
                StoredProduct.find {
                    (StoredProducts.enabled eq true) and (StoredProducts.active eq true)
                }.count()
            }
            call.respond(products)
        }
        authenticate("auth-jwt-user") {
            post<ProductRequest> {
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                upsertRequest(null)
            }
        }
        get<ProductRequest.Id> { id ->
            dbQuery {
                val candidate = StoredProduct.findById(id.id)

                if (candidate != null) {
                    call.respond(mapToResponse(candidate))
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        authenticate("auth-jwt-user") {
            put<ProductRequest.Id> { id ->
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }

                upsertRequest(id.id)
            }
            delete<ProductRequest.Id> { id ->
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@delete
                }

                dbQuery {
                    val candidate = StoredProduct.findById(id.id)

                    if (candidate != null) {
                        candidate.enabled = false

                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }

            post<ProductRequest.Id.Notify> { notifyReq ->
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()

                dbQuery {
                    StoredUserProductNotifications.insertIgnore {
                        it[user] = userId
                        it[product] = notifyReq.parent.id
                    }
                }

                val notReqInfo = call.receive<ProductNotificationNew>()

                if (notReqInfo.phone != null) {
                    val product = dbQuery {
                        mapToResponse(StoredProduct.findById(notifyReq.parent.id)!!)
                    }

                    message {
                        """
                    |Пользователь ${notReqInfo.name ?: ""} просит уведомить его о поступлении товара ${product.name}
                    |Телефон: ${notReqInfo.phone ?: "НЕ УКАЗАН"}
                    |""".trimMargin()
                    }.options {
                        parseMode = ParseMode.Markdown
                    }.send(NOTIFY_CHAT_ID, bot)
                }

                call.respond(HttpStatusCode.Created)
            }

            delete<ProductRequest.Id.Notify> { notifyReq ->
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()

                dbQuery {
                    StoredUserProductNotifications.deleteWhere {
                        (user eq userId) and (product eq notifyReq.parent.id)
                    }
                }

                call.respond(HttpStatusCode.NoContent)
            }

            get<ProductRequest.Notifications> { notreq ->
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("user-id").asLong()

                var query: Op<Boolean> = Op.TRUE

                query = query and (StoredUserProductNotifications.user eq userId)

                notreq.inStock?.let {
                    query = query and (StoredProducts.inStock eq notreq.inStock)
                }

                val products = dbQuery {
                    StoredUserProductNotifications.leftJoin(StoredProducts,
                        {product},
                        {StoredProducts.id}).selectAll().where {
                        query
                    }.map {
                        it[StoredUserProductNotifications.product].value
                    }

                }

                call.respond(products)
            }

        }

    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.upsertRequest(id: Long?) {
    val newProduct = call.receive<ProductNew>()

    dbQuery {
        val foundCategories = StoredCategory.find {
            StoredCategories.id inList newProduct.categoriesId.map { id -> EntityID(id, StoredImages) }
        }

        val foundImages = StoredImage.find {
            StoredImages.id inList newProduct.imageIds.map { id -> EntityID(id, StoredImages) }
        }

        val foundLabels = StoredLabel.find {
            StoredLabels.id inList newProduct.labelIds.map { id -> EntityID(id, StoredLabels) }
        }

        if (foundCategories.toList().size == newProduct.categoriesId.size &&
            foundImages.toList().size == newProduct.imageIds.size &&
            foundLabels.toList().size == newProduct.labelIds.size) {
            val candidate = id?.let {
                StoredProduct.findById(id)
            }

            if (candidate == null) {
                val newStoredProduct = StoredProduct.new {
                    name = newProduct.name
                    description = newProduct.description
                    categories = foundCategories
                    price = newProduct.price
                    active = newProduct.active
                    inStock = newProduct.inStock
                    images = foundImages
                    labels = foundLabels
                    createdAt = LocalDate.now()
                    quantity = newProduct.quantity
                    unit = newProduct.unit
                }

                call.response.status(HttpStatusCode.Created)
                call.respond(mapToResponse(newStoredProduct))
            }
            else {


                candidate.name = newProduct.name
                candidate.description = newProduct.description
                candidate.priceOld = candidate.price
                candidate.price = newProduct.price
                candidate.labels = foundLabels
                candidate.active = newProduct.active
                candidate.inStock = newProduct.inStock
                candidate.images = foundImages
                candidate.categories = foundCategories
                candidate.quantity = newProduct.quantity
                candidate.unit = newProduct.unit

                call.respond(HttpStatusCode.NoContent)
            }
        } else {
            call.respondText(
                "Nothing was found, idiot",
                status = HttpStatusCode.NotFound
            )

        }

    }
}