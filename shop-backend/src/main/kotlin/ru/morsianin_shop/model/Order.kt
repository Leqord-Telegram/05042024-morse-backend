package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    val id: Long,
    val items: List<OrderItemResponse>,
    val status: OrderStatus
) {

}

@Serializable
data class OrderItemResponse(
    val product: Product,
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