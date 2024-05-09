package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long?,
    val name: String,
    val privileges: Set<UserPrivilege>,
    val cart: List<UserCartItemResponse>,
)

@Serializable
data class UserCartItemResponse(val product: ProductResponse, val quantity: Long)

@Serializable
enum class UserPrivilege(val value: String) {
    ADMIN("admin"),
    SUPPORT("support"),
}