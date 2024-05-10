package ru.morsianin_shop.model

import kotlinx.serialization.Serializable


@Serializable
data class CartItemResponse(
    val id: Long,
    val product: ProductResponse,
    val quantity: Long
)

@Serializable
data class CartItemRequest(
    val productId: Long,
    val quantity: Long
)