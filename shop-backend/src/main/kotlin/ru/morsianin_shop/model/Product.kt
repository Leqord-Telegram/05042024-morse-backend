package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val category: CategoryResponse,
    val price: Long,
    val quantity: Long,
    val active: Boolean,
    val imageResponses: List<Image>
)
