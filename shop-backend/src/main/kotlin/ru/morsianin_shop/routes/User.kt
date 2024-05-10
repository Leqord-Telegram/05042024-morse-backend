package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.resources.UserRequest
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredUser
import ru.morsianin_shop.storage.StoredUsers

fun Application.userRoutes() {
    routing {
        get<UserRequest> { filter ->
            var query: Op<Boolean> = Op.TRUE

            filter.name?.let {
                query = query and (StoredUsers.name eq it)
            }

            dbQuery {
                val found = StoredUser.find {
                    query
                }

                if (!found.empty()) {
                    call.respond(found.map { mapToResponse(it) }.toList())
                }
                else {
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
        get<UserRequest.Id> { id ->
            dbQuery {
                val found = StoredUser.findById(id.id)

                if (found != null) {
                    call.respond(mapToResponse(found))
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}