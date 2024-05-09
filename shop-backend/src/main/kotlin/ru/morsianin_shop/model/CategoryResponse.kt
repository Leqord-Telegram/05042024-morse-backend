package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: Long,
    val name: String
)

@Serializable
data class CategoryNew(
    val name: String
)