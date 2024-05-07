package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class Cart(
    val userId: Long,
    val items: List<Item>,
) {
    @Serializable
    data class Item(
        val productId: Long,
        val quantity: Int,
    )
}