package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.morsianin_shop.model.Order
import ru.morsianin_shop.resources.Users

object StoredOrders: IntIdTable() {
    val user = reference("userId", StoredUsers)
    val status = enumerationByName<Order.Status>("status", 255)
}

class StoredOrder(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StoredOrder>(StoredOrders)
    var user by StoredUser referencedOn StoredOrders.user
    var status by StoredOrders.status
}