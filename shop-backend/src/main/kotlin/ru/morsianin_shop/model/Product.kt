package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val category: CategoryResponse,
    val price: Long,
    val quantity: Long,
    val active: Boolean,
    val images: List<ImageResponse>
)

@Serializable
data class ProductNew(
    val name: String,
    val description: String,
    val categoryId: Long,
    val price: Long,
    val quantity: Long,
    val active: Boolean,
    val imageIds: List<Long>
)
