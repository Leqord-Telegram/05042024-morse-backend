package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderDTO(
    val id: Long,
    val items: List<OrderItem>,
    val status: OrderStatus
) {

}

@Serializable
data class OrderItem(
    val product: ProductDTO,
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