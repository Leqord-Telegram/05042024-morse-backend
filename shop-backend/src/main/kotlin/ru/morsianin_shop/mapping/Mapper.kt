package ru.morsianin_shop.mapping

import ru.morsianin_shop.model.*
import ru.morsianin_shop.storage.*

object Mapper {
    fun mapToDTO(stored: StoredCategory): CategoryDTO = CategoryDTO(
        id = stored.id.value,
        name = stored.name
    )

    fun mapToDTO(stored: StoredImage): ImageDTO = ImageDTO(
        id = stored.id.value,
        url = stored.url
    )

    fun mapToDTO(stored: StoredProduct): ProductDTO = ProductDTO(
        id = stored.id.value,
        name = stored.name,
        description = stored.description?: "",
        category = mapToDTO(stored.category),
        price = stored.price,
        quantity = stored.quantity,
        active = stored.active,
        images = stored.images.map { mapToDTO(it) }
    )

    fun mapToDTO(stored: StoredOrderItem): OrderItemDTO = OrderItemDTO(
        product = mapToDTO(stored.product),
        quantity = stored.quantity,
    )


    fun mapToDTO(stored: StoredOrder): OrderDTO = OrderDTO(
            id = stored.id.value,
            items = stored.items.map { mapToDTO(it) },
            status = stored.status
    )

    fun mapToDTO(stored: StoredUserCartItem): UserCartItemDTO = UserCartItemDTO(
        product = mapToDTO(stored.product),
        quantity = stored.quantity
    )

    fun mapToDTO(stored: StoredUser): UserDTO {
        stored.privileges
        return UserDTO(
            id = stored.id.value,
            name = stored.name,
            privileges = stored.privileges.map { it.privilege }.toSet(),
            cart = stored.cartItems.map { mapToDTO(it) }
        )
    }


}