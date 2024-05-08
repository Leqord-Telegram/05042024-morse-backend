package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Long,
    val name: String,
    val username: String,
    val privileges: Set<Privilege>,
    val cart: List<CartItem>,
) {

    @Serializable
    data class CartItem(val productId: Long, val quantity: Long)

    @Serializable
    enum class Privilege(val value: String) {
        ADMIN("admin"),
    }
}