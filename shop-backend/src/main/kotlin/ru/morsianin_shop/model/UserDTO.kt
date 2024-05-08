package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Long,
    val name: String,
    val username: String,
    val privileges: Set<UserPrivilegeDTO>,
    val cart: List<UserCartItemDTO>,
)

@Serializable
data class UserCartItemDTO(val product: ProductDTO, val quantity: Long)

@Serializable
enum class UserPrivilegeDTO(val value: String) {
    ADMIN("admin"),
}