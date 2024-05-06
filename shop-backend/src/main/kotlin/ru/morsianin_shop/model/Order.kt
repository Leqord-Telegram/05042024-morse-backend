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
        val quantity: Long
    )

    @Serializable
    enum class Status {
        Failed, Pending, Shipping, Arrived, Finished
    }
}