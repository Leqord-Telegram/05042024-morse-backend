package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    val id: Long,
    val storedId: String,
    val format: ImageFormat,
)

@Serializable
enum class ImageFormat(val mime: String) {
    Png("image/png"),
    Jpeg("image/jpeg")
}