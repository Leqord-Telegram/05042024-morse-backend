package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageDTO(
    val id: Long?,
    val url: String
)