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
import ru.morsianin_shop.resources.Categories
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredCategories
import ru.morsianin_shop.storage.StoredCategory

fun Application.categoryRoutes() {
    routing {
        get<Categories> { filter ->
            var query: Op<Boolean> = Op.TRUE

            filter.name?.let {
                query = query and (StoredCategories.name eq it)
            }

            val response = dbQuery {
                    StoredCategory.find { query }.map { category -> mapToDTO(category) }
                }
            call.respond(response)
        }
        post<Categories> {
            val newCategory = call.receive<CategoryNew>()

            dbQuery {
                StoredCategory.new {
                    name = newCategory.name
                }
            }

            call.respond(HttpStatusCode.Created)
        }
        get<Categories.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        put<Categories.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
        delete<Categories.Id> { id ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
        }
    }
}