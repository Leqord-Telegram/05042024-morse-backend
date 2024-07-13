package ru.morsianin_shop.mapping

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.year
import ru.morsianin_shop.model.*
import ru.morsianin_shop.storage.*
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery

object Mapper {
    fun mapToResponse(stored: StoredCategory): CategoryResponse = CategoryResponse(
        id = stored.id.value,
        name = stored.name,
        orderPriority = stored.orderPriority
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
        category = stored.categories.map { mapToResponse(it) },
        price = stored.price,
        quantity = stored.quantity,
        active = stored.active,
        priceOld = stored.priceOld?.let {
            if(stored.price < stored.priceOld!!) {
                stored.priceOld
            }
            else {
                null
            }
        },
        createdAt = stored.createdAt,
        images = stored.images.map { mapToResponse(it) },
        labels = stored.labels.map { mapToResponse(it) },
        unit = stored.unit
    )

    fun mapToResponse(stored: StoredOrderItem): OrderItemResponse = OrderItemResponse(
        id = stored.id.value,
        product = mapToResponse(stored.product),
        quantity = stored.quantity,
    )

    suspend fun generateSpecificId(order: StoredOrder): String {
        val year = order.shipmentDateTime.year % 100 // последние две цифры года
        val ordersInYear = dbQuery {
            StoredOrder.find { StoredOrders.shipmentDateTime.year() eq order.shipmentDateTime.year }
                .orderBy(StoredOrders.shipmentDateTime to SortOrder.ASC)
                .toList()
        }

        val orderIndex = ordersInYear.indexOfFirst { it.id == order.id }
        if (orderIndex == -1) {
            throw IllegalStateException("Order not found in the list of orders for the year.")
        }

        return "${orderIndex + 1}-$year"
    }

    fun mapToResponse(stored: StoredOrder): OrderResponse {

        return OrderResponse(
            id = stored.id.value,
            items = stored.items.map { mapToResponse(it) },
            status = stored.status,
            userName = stored.userName,
            description = stored.description?: "",
            shipment = stored.shipment,
            shipmentAddress = stored.shipmentAddress,
            shipmentDateTime = stored.shipmentDateTime,
            phone = stored.phone,
            shittyId = runBlocking { generateSpecificId(stored) }
        )
    }

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

    fun mapToResponse(stored: StoredLabel): LabelResponse {
        return LabelResponse(
            id = stored.id.value,
            name = stored.name,
            color = LabelColorRGB(
                red = stored.colorRed.toUByte(),
                green = stored.colorGreen.toUByte(),
                blue = stored.colorBlue.toUByte(),
            )
        )
    }

}