package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductDTO(
    val id: Long,
    val name: String,
    val description: String,
    val categoryId: Long,
    val price: Long,
    val quantity: Long,
    val active: Boolean,
    val images: List<ImageDTO>
)
