package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    val admin: Boolean,
    val cart: Cart
) {
    @Serializable
    data class Cart(val items: List<Item> = emptyList()) {
        @Serializable
        data class Item(val productId: Long, val quantity: Long)
    }
}