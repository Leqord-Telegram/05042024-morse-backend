package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import ru.morsianin_shop.model.Order
import ru.morsianin_shop.resources.Users

object StoredOrders: LongIdTable() {
    val user = reference("userId", StoredUsers)
    val status = enumerationByName<Order.Status>("status", 255)
}

class StoredOrder(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredOrder>(StoredOrders)
    var user by StoredUser referencedOn StoredOrders.user
    var status by StoredOrders.status
}