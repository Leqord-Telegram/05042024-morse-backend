package ru.morsianin_shop.model

import kotlinx.serialization.Serializable


@Serializable
data class CartItemResponse(
    val id: Long,
    val userId: Long,
    val product: ProductResponse,
    val quantity: Long)