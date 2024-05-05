package ru.morsianin_shop.model

import kotlinx.serialization.*

@Serializable
data class Category(
    val id: Long,
    val name: String
)

@Serializable
data class CategoryRequest(
    val id: Long? = null,
    val name: String? = null
)

@Serializable
data class Image(
    val id: Long
)

@Serializable
data class OrderItem(
    val productId: Long,
    val quantity: Long
)

@Serializable
data class Order(
    val id: Long,
    val userId: Long,
    val items: List<OrderItem>,
    val status: OrderStatus
)

@Serializable
data class OrderRequest(
    val id: Long? = null,
    val userId: Long? = null,
    //val items: List<OrderItemRequest>? = null,
    val status: OrderStatus? = null
)

@Serializable
data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val categoryId: Long,
    val price: Long,
    val quantity: Long,
    val active: Boolean,
    val images: List<Image>
)

@Serializable
data class ProductRequest(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val categoryId: Long? = null,
    val price: Long? = null,
    val quantity: Long? = null,
    val active: Boolean? = null,
    val images: List<Image>? = null
)

@Serializable
data class User(
    val id: Long,
    val name: String,
    val admin: Boolean
)

@Serializable
data class UserRequest(
    val id: Long? = null,
    val name: String? = null,
    val admin: Boolean? = null
)

@Serializable
enum class OrderStatus {
    Failed, Pending, Shipping, Arrived, Finished
}
