package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderDTO(
    val id: Long?,
    val items: List<OrderItemDTO>,
    val status: OrderStatusDTO
) {

}

@Serializable
data class OrderItemDTO(
    val product: ProductDTO,
    val quantity: Int
)

@Serializable
enum class OrderStatusDTO(val value: String) {
    FAILED("failed"),
    PENDING("pending"),
    SHIPPING("shipping"),
    ARRIVED("arrived"),
    FINISHED("finished")
}