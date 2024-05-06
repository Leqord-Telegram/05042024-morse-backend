package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    val admin: Boolean,
    val cart: List<Order.Item>
)