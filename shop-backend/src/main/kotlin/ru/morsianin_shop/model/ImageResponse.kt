package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    val id: Long,
    val url: String
)