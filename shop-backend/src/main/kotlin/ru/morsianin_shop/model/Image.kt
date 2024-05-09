package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: Long,
    val url: String
)