package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import ru.morsianin_shop.model.OrderDTO
import ru.morsianin_shop.model.OrderStatusDTO
import ru.morsianin_shop.storage.StoredOrder.Companion.referrersOn

object StoredOrders: LongIdTable("order") {
    val user = reference("user_id", StoredUsers)
    val status = enumerationByName<OrderStatusDTO>("status", 255)
}

class StoredOrder(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredOrder>(StoredOrders)
    var user by StoredUser referencedOn StoredOrders.user
    var status by StoredOrders.status
    val items by StoredOrderItem referrersOn StoredOrderItems.order
}