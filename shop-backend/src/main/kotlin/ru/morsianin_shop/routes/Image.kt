package ru.morsianin_shop.routes

import io.ktor.http.*
import io.ktor.http.content.*
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

                val multipartData = call.receiveMultipart()

                var imageBytes: ByteArray? = null
                var imageFormat: ImageFormat? = null

                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            val contentType = part.contentType
                            imageFormat = when (contentType?.toString()) {
                                "image/jpeg", "image/jpg" -> ImageFormat.Jpeg
                                "image/png" -> ImageFormat.Png
                                else -> null
                            }

                            if (imageBytes == null) {
                                imageBytes = part.streamProvider().readBytes()
                            }
                            else {
                                imageBytes = imageBytes!! + part.streamProvider().readBytes()
                            }
                        }

                        else -> {}
                    }
                    part.dispose()
                }

                if (imageBytes == null || imageFormat == null) {
                    call.respond(HttpStatusCode.BadRequest, "No image file found in the request")
                    return@post
                }

                val hash = computeSHA256Hash(imageBytes!!)
                val filename = "$hash.${imageFormat!!.extension}"

                dbQuery {
                    val candidate = StoredImage.all().find { img -> img.storedId == filename }

                    if (candidate != null) {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(ResultCreated(id = candidate.id.value))
                    } else {
                        val created = StoredImage.new {
                            storedId = filename
                            format = imageFormat!!
                        }

                        FileStorage.saveFile(filename, imageBytes!!)

                        call.response.status(HttpStatusCode.Created)
                        call.respond(ResultCreated(id = created.id.value))
                    }
                }

            }
        }
        get<ImageRequest.Id> { id ->
            val imageCandidate = dbQuery {
                StoredImage.findById(id.id)?.let {
                    mapToResponse(it)
                }
            }

            if (imageCandidate == null) {
                call.respond(HttpStatusCode.NotFound)
            }
            call.respondFile(FileStorage.getFile(imageCandidate!!.storedId))
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