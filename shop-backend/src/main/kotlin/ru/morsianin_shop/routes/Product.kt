package ru.morsianin_shop.routes

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
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.ProductNew
import ru.morsianin_shop.model.UserPrivilege
import ru.morsianin_shop.plugins.hasPrivilege
import ru.morsianin_shop.resources.ProductRequest
import ru.morsianin_shop.storage.*
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery

fun Application.productRoutes() {
    routing {
        get<ProductRequest> { filter ->
            var query: Op<Boolean> = Op.TRUE

            filter.name?.let {
                query = query and (StoredProducts.name eq it)
            }

            filter.description?.let {
                query = query and (StoredProducts.name eq it)
            }

            filter.categoryId?.let {
                query = query and (StoredProducts.category eq it)
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

            val found = dbQuery {
                StoredProduct.find {
                    query
                }.map { mapToResponse(it) }
            }

            if (found.isNotEmpty()) {
                call.respond(found)
            }
            else {
                call.respond(HttpStatusCode.NotFound)
            }
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
                    call.respond(candidate)
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

private suspend fun PipelineContext<Unit, ApplicationCall>.upsertRequest(id: Long?) {
    val newProduct = call.receive<ProductNew>()

    dbQuery {
        val foundCategory = StoredCategory.findById(newProduct.categoryId)

        val foundImages = StoredImage.find {
            StoredImages.id inList newProduct.imageIds.map { id -> EntityID(id, StoredImages) }
        }

        if (foundCategory != null && foundImages.toList().size == newProduct.imageIds.size) {
            if (id == null) {
                val newStoredProduct = StoredProduct.new {
                    name = newProduct.name
                    description = newProduct.description
                    category = foundCategory
                    price = newProduct.price
                    active = newProduct.active
                    images = foundImages
                    quantity = newProduct.quantity
                }

                call.response.status(HttpStatusCode.Created)
                call.respond(mapToResponse(newStoredProduct))
            }
            else {
                val candidate = StoredProduct.findById(id)

                if (candidate != null) {
                    candidate.name = newProduct.name
                    candidate.description = newProduct.description
                    candidate.price = newProduct.price
                    candidate.active = newProduct.active
                    candidate.images = foundImages
                    candidate.category = foundCategory
                    candidate.quantity = newProduct.quantity

                    call.respond(HttpStatusCode.NoContent)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        } else {
        if(foundCategory == null) {
            call.respondText(
                "category with id ${newProduct.categoryId} not found",
                status = HttpStatusCode.NotFound
            )
        }
            else {
            call.respondText(
                "Not all images were not found",
                status = HttpStatusCode.NotFound
            )
            }
        }

    }
}