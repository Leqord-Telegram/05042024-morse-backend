package ru.morsianin_shop.model

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: Long,
    val url: String
)