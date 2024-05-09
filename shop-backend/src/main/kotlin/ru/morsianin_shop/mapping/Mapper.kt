package ru.morsianin_shop.mapping

import ru.morsianin_shop.model.*
import ru.morsianin_shop.storage.*

object Mapper {
    fun mapToDTO(stored: StoredCategory): CategoryResponse = CategoryResponse(
        id = stored.id.value,
        name = stored.name
    )

    fun mapToDTO(stored: StoredImage): ImageResponse = ImageResponse(
        id = stored.id.value,
        url = stored.url
    )

    fun mapToDTO(stored: StoredProduct): ProductResponse = ProductResponse(
        id = stored.id.value,
        name = stored.name,
        description = stored.description?: "",
        category = mapToDTO(stored.category),
        price = stored.price,
        quantity = stored.quantity,
        active = stored.active,
        imageResponses = stored.images.map { mapToDTO(it) }
    )

    fun mapToDTO(stored: StoredOrderItem): OrderItemResponse = OrderItemResponse(
        product = mapToDTO(stored.product),
        quantity = stored.quantity,
    )


    fun mapToDTO(stored: StoredOrder): OrderResponse = OrderResponse(
            id = stored.id.value,
            items = stored.items.map { mapToDTO(it) },
            status = stored.status
    )

    fun mapToDTO(stored: StoredUserCartItem): UserCartItemResponse = UserCartItemResponse(
        product = mapToDTO(stored.product),
        quantity = stored.quantity
    )

    fun mapToDTO(stored: StoredUser): UserResponse {
        return UserResponse(
            id = stored.id.value,
            name = stored.name,
            privileges = stored.privileges.map { it.privilege }.toSet(),
            cart = stored.cartItems.map { mapToDTO(it) }
        )
    }


}