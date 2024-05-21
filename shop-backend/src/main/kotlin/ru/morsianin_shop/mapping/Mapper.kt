package ru.morsianin_shop.mapping

import ru.morsianin_shop.model.*
import ru.morsianin_shop.storage.*

object Mapper {
    fun mapToResponse(stored: StoredCategory): CategoryResponse = CategoryResponse(
        id = stored.id.value,
        name = stored.name
    )

    fun mapToResponse(stored: StoredImage): ImageResponse = ImageResponse(
        id = stored.id.value,
        storedId = stored.storedId,
        format = stored.format
    )

    fun mapToResponse(stored: StoredProduct): ProductResponse = ProductResponse(
        id = stored.id.value,
        name = stored.name,
        description = stored.description?: "",
        category = mapToResponse(stored.category),
        price = stored.price,
        quantity = stored.quantity,
        active = stored.active,
        priceOld = stored.priceOld,
        createdAt = stored.createdAt,
        images = stored.images.map { mapToResponse(it) }
    )

    fun mapToResponse(stored: StoredOrderItem): OrderItemResponse = OrderItemResponse(
        id = stored.id.value,
        product = mapToResponse(stored.product),
        quantity = stored.quantity,
    )

    fun mapToResponse(stored: StoredOrder): OrderResponse = OrderResponse(
            id = stored.id.value,
            items = stored.items.map { mapToResponse(it) },
            status = stored.status
    )

    fun mapToResponse(stored: StoredUserCartItem): CartItemResponse = CartItemResponse(
        id = stored.id.value,
        product = mapToResponse(stored.product),
        quantity = stored.quantity,
    )


    fun mapToResponse(stored: StoredUser): UserResponse {
        return UserResponse(
            id = stored.id.value,
            name = stored.name,
            privileges = stored.privileges.map { it.privilege }.toSet(),
        )
    }


}