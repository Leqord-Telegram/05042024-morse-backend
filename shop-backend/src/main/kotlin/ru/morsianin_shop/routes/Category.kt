package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.mapping.Mapper.mapToDTO
import ru.morsianin_shop.model.CategoriesDTO
import ru.morsianin_shop.resources.Categories
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredCategory

fun Application.categoryRoutes() {
    routing {
        get<Categories> { categories ->
            val response = dbQuery {
                StoredCategory.find {
                    Stored
                }
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