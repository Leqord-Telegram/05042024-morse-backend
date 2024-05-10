package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    val id: Long,
    val items: List<OrderItemResponse>,
    val status: OrderStatus
)

@Serializable
data class OrderItemResponse(
    val id: Long,
    val product: ProductResponse,
    val quantity: Int
)

@Serializable
data class OrderNew(
    val items: List<OrderItemNew>,
    val status: OrderStatus
)

@Serializable
data class OrderItemNew(
    val productId: Long,
    val quantity: Int
)

@Serializable
data class OrderItemChanged(
    val quantity: Int
)

@Serializable
enum class OrderStatus(val value: String) {
    FAILED("failed"),
    PENDING("pending"),
    SHIPPING("shipping"),
    ARRIVED("arrived"),
    FINISHED("finished")
}