package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.CategoryNew
import ru.morsianin_shop.model.UserPrivilege
import ru.morsianin_shop.plugins.hasPrivilege
import ru.morsianin_shop.resources.CategoryRequest
import ru.morsianin_shop.storage.*
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredProductCategories.category

fun Application.categoryRoutes() {
    routing {
        get<CategoryRequest> { filter ->
            var query: Op<Boolean> = Op.TRUE

            query = query and (StoredCategories.enabled eq true)

            filter.name?.let {
                query = query and (StoredCategories.name eq it)
            }

            val response = dbQuery {
                    StoredCategory.find { query }.map { category -> mapToResponse(category) }
                }

            if(response.isNotEmpty()) {
                call.respond(response)
            }
            else {
                call.respond(HttpStatusCode.NotFound)
            }

        }

        authenticate("auth-jwt-user") {
            post<CategoryRequest> {
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                val newCategory = call.receive<CategoryNew>()
                val newStoredCategory = dbQuery {
                    StoredCategory.new {
                        name = newCategory.name
                    }
                }
                call.response.status(HttpStatusCode.Created)
                call.respond(mapToResponse(newStoredCategory))
            }
        }
        get<CategoryRequest.Id> { id ->
            var query: Op<Boolean> = Op.TRUE

            query = query and (StoredCategories.id eq id.id)

            id.parent.name?.let {
                query = query and (StoredCategories.name eq it)
            }

            val response = dbQuery {
                StoredCategory.find { query }.map { category -> mapToResponse(category) }.singleOrNull()
            }

            call.respond(response?: HttpStatusCode.NotFound)
        }
        get<CategoryRequest.Id.Total> { total ->
            val products = dbQuery {
                StoredProducts.innerJoin(StoredProductCategories).select(StoredProducts.columns).where {
                    (StoredProducts.enabled eq true) and (StoredProducts.active eq true) and (category eq total.parent.id)
                }.count()
            }

            call.respond(products)
        }
        authenticate("auth-jwt-user") {
            put<CategoryRequest.Id> { id ->
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }

                val newCategory = call.receive<CategoryNew>()

                dbQuery {
                    val candidate = StoredCategory.findById(id.id)

                    if (candidate != null) {
                        candidate.name = newCategory.name
                        call.respond(HttpStatusCode.OK)
                    } else {
                        val createdCategory = StoredCategory.new(id.id) {
                            name = newCategory.name
                        }
                        call.response.status(HttpStatusCode.Created)
                        call.respond(mapToResponse(createdCategory))
                    }
                }
            }
            delete<CategoryRequest.Id> { id ->
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@delete
                }

                dbQuery {
                    val candidate = StoredCategory.findById(id.id)
                    if (candidate != null) {
                        candidate.enabled = false

                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }

                }
            }
        }
    }
}