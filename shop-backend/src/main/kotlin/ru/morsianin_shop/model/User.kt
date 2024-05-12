package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val name: String,
    val privileges: Set<UserPrivilege>,
)

@Serializable
enum class UserPrivilege {
    ADMIN,
    SUPPORT,
    USER,
}