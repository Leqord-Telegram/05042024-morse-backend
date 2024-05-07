package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Long,
    val userId: Long,
    val items: List<Item>,
    val status: Status
) {
    @Serializable
    data class Item(
        val productId: Long,
        val quantity: Int
    )

    @Serializable
    enum class Status(val value: String) {
        FAILED("failed"),
        PENDING("pending"),
        SHIPPING("shipping"),
        ARRIVED("arrived"),
        FINISHED("finished")
    }
}