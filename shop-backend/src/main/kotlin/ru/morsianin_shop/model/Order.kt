package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val productId: Long,
    val quantity: Long
)

@Serializable
enum class OrderStatus {
    Failed, Pending, Shipping, Arrived, Finished
}

@Serializable
data class Order(
    val id: Long,
    val userId: Long,
    val items: List<OrderItem>,
    val status: OrderStatus
)