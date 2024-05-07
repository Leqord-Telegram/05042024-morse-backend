package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    val username: String,
    val privileges: Set<Privilege>,
    val cart: Cart
) {
    @Serializable
    data class Cart(val items: List<Item> = emptyList()) {
        @Serializable
        data class Item(val productId: Long, val quantity: Long)
    }
    @Serializable
    enum class Privilege(val value: String) {
        ADMIN("admin"),
    }
}