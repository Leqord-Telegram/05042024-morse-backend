package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import ru.morsianin_shop.mapping.Mapper.mapToResponse
import ru.morsianin_shop.model.ImageFormat
import ru.morsianin_shop.model.ResultCreated
import ru.morsianin_shop.model.UserPrivilege
import ru.morsianin_shop.plugins.hasPrivilege
import ru.morsianin_shop.resources.ImageRequest
import ru.morsianin_shop.storage.*
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery


fun Application.imageRoutes() {
    routing {
        get<ImageRequest> {
            dbQuery {
                call.respond(StoredImage.all().map { img -> mapToResponse(img) })
            }
        }
        authenticate("auth-jwt-user") {
            post<ImageRequest> {
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                val contentType = call.request.contentType()
                val imageFormat = when (contentType.toString()) {
                    "image/jpeg", "image/jpg" -> ImageFormat.Jpeg
                    "image/png" -> ImageFormat.Png
                    else -> null
                }

                if (imageFormat == null) {
                    call.respondText("Unknown content type $contentType", status = HttpStatusCode.BadRequest)
                    return@post
                }

                val fileBytes: ByteArray = call.receive<ByteArray>()
                val hash = computeSHA256Hash(fileBytes)
                val filename = "$hash.${contentType.contentSubtype}"

                dbQuery {
                    val candidate = StoredImage.all().find { img -> img.storedId == filename }

                    if (candidate != null) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ResultCreated(id = candidate.id.value))
                    } else {
                        val created = StoredImage.new {
                            storedId = filename
                            format = imageFormat
                        }
                        putS3Object(filename, fileBytes, imageFormat.mime)
                        call.response.status(HttpStatusCode.Created)
                        call.respond(ResultCreated(id = created.id.value))
                    }
                }

            }
        }
        get<ImageRequest.Id> { id ->
            val storageUrl = S3ImageStorage.getHost()

            dbQuery {
                val candidate = StoredImage.findById(id.id)

                if (candidate != null) {
                    call.respondRedirect("${storageUrl}/${candidate.storedId}")
                }
                else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        authenticate("auth-jwt-user") {
            delete<ImageRequest.Id> { id ->
                if (!hasPrivilege(call.principal<JWTPrincipal>()!!.payload, UserPrivilege.ADMIN)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@delete
                }

                dbQuery {
                    val candidate = StoredImage.findById(id.id)

                    if (candidate != null) {
                        deleteBucketObjects(candidate.storedId)
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