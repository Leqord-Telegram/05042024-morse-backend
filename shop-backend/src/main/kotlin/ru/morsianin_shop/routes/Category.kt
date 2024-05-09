package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import ru.morsianin_shop.mapping.Mapper.mapToDTO
import ru.morsianin_shop.model.CategoryNew
import ru.morsianin_shop.resources.CategoryRequest
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredCategories
import ru.morsianin_shop.storage.StoredCategory

fun Application.categoryRoutes() {
    routing {
        get<CategoryRequest> { filter ->
            var query: Op<Boolean> = Op.TRUE

            filter.name?.let {
                query = query and (StoredCategories.name eq it)
            }

            val response = dbQuery {
                    StoredCategory.find { query }.map { category -> mapToDTO(category) }
                }

            if(response.isNotEmpty()) {
                call.respond(response)
            }
            else {
                call.respond(HttpStatusCode.NotFound)
            }

        }
        post<CategoryRequest> {
            val newCategory = call.receive<CategoryNew>()
            dbQuery {
                StoredCategory.new {
                    name = newCategory.name
                }
            }
            call.respond(HttpStatusCode.Created)
        }
        get<CategoryRequest.Id> { id ->
            var query: Op<Boolean> = Op.TRUE

            query = query and (StoredCategories.id eq id.id)

            id.parent.name?.let {
                query = query and (StoredCategories.name eq it)
            }

            val response = dbQuery {
                StoredCategory.find { query }.map { category -> mapToDTO(category) }.singleOrNull()
            }

            call.respond(response?: HttpStatusCode.NotFound)
        }
        put<CategoryRequest.Id> { id ->
            val newCategory = call.receive<CategoryNew>()

            dbQuery {
                val candidate = StoredCategory.findById(id.id)

                if (candidate != null) {
                    candidate.name = newCategory.name
                    call.respond(HttpStatusCode.OK)
                }
                else {
                    StoredCategory.new(id.id) {
                        name = newCategory.name
                    }
                    call.respond(HttpStatusCode.Created)
                }
            }
        }
        delete<CategoryRequest.Id> { id ->
            dbQuery {
                val candidate = StoredCategory.findById(id.id)
                if (candidate != null) {
                    candidate.delete()
                    call.respond(HttpStatusCode.OK)
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }

            }
        }
    }
}