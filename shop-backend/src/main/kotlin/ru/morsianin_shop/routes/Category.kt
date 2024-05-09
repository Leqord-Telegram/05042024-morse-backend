package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import ru.morsianin_shop.mapping.Mapper.mapToDTO
import ru.morsianin_shop.model.CategoriesDTO
import ru.morsianin_shop.resources.Categories
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredCategories
import ru.morsianin_shop.storage.StoredCategory
import javax.management.Query.eq

fun Application.categoryRoutes() {
    routing {
        get<Categories> { filter ->
            val response = dbQuery {
                    var query: Op<Boolean> = Op.TRUE

                    filter.name?.let {
                        query = query and (StoredCategories.name eq it)
                    }

                    StoredCategory.find { query }
                }
            call.respond(response)
        }
        post<Categories> { category ->
            call.respondText("tbi", status = HttpStatusCode.NotImplemented)
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