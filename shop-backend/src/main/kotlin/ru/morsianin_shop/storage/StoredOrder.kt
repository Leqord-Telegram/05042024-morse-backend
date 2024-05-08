package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import ru.morsianin_shop.model.OrderDTO

object StoredOrders: LongIdTable("order") {
    val user = reference("user_id", StoredUsers)
    val status = enumerationByName<OrderDTO.Status>("status", 255)
}

class StoredOrder(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredOrder>(StoredOrders)
    var user by StoredUser referencedOn StoredOrders.user
    var status by StoredOrders.status
}