package ru.morsianin_shop.storage

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.*
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.net.url.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.morsianin_shop.storage.S3ImageStorage.getBucketName
import ru.morsianin_shop.storage.S3ImageStorage.getEndpoint
import ru.morsianin_shop.storage.S3ImageStorage.getRegion
import java.security.MessageDigest

object S3ImageStorage {
    fun getBucketName(): String {
        return System.getenv("AWS_IMAGE_BUCKET") ?: "images.morsianin-shop.ru"
    }

    fun getHost(): String {
        return System.getenv("AWS_IMAGE_HOST") ?: "http://images.morsianin-shop.ru"
    }

    fun getEndpoint(): String {
        return System.getenv("AWS_ENDPOINT")?: "https://storage.yandexcloud.net"
    }

    fun getRegion(): String {
        return System.getenv("AWS_REGION")?: "ru-central1"
    }
}

suspend fun putS3Object(objectKey: String,
                        bytes: ByteArray,
                        contentTypeVal: String,
                        objectMetadata: Map<String, String> = mutableMapOf<String, String>(),
                        ): PutObjectResponse {
    val request = PutObjectRequest {
        bucket = getBucketName()
        contentType = contentTypeVal
        key = objectKey
        metadata = objectMetadata
        body = ByteStream.fromBytes(bytes)
    }

    S3Client {
        region = getRegion()
        endpointUrl = Url.parse(getEndpoint())
    }.use { s3 ->
        return s3.putObject(request)
    }
}

suspend fun getObjectBytes(objectKey: String): ByteStream? {
    val request = GetObjectRequest {
        key = objectKey
        bucket = getBucketName()
    }
    return S3Client {
        region = getRegion()
        endpointUrl = Url.parse(getEndpoint())
    }.use { s3 ->
        s3.getObject(request) { resp ->
            return@getObject resp.body
        }
    }
}

suspend fun deleteBucketObjects(objectName: String): DeleteObjectsResponse {
    val objectId = ObjectIdentifier {
        key = objectName
    }

    val delOb = Delete {
        objects = listOf(objectId)
    }

    val request = DeleteObjectsRequest {
        bucket = getBucketName()
        delete = delOb
    }

    return S3Client {
        region = getRegion()
        endpointUrl = Url.parse(getEndpoint())
    }.use { s3 ->
        return@use s3.deleteObjects(request)
    }
}

suspend fun computeSHA256Hash(fileBytes: ByteArray): String {
    return withContext(Dispatchers.Default) {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(fileBytes)
        hashBytes.joinToString("") { "%02x".format(it) }
    }
}