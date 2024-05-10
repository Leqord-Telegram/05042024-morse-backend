package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class Cart(
    val userId: Long,
    val items: List<CartItemResponse>
)

@Serializable
data class CartItemResponse(
    val id: Long,
    val product: ProductResponse,
    val quantity: Long)