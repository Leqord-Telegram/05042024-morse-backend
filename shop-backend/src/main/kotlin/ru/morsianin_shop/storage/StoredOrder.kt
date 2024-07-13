package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.timestamp
import ru.morsianin_shop.model.OrderShipment
import ru.morsianin_shop.model.OrderStatus
import java.time.LocalDateTime

object StoredOrders: LongIdTable("order") {
    val user = reference("user_id", StoredUsers)
    val status = enumerationByName<OrderStatus>("status", 255)
    val shipment = enumerationByName<OrderShipment>("shipment", 255)
    val shipmentDateTime = datetime("shipment_date_time")
    val shipmentAddress = text("shipment_address").nullable()
    val description = text("description").nullable()
    val phone = text("phone").nullable()
    val userName = text("user_name")
}

class StoredOrder(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredOrder>(StoredOrders)
    var user by StoredUser referencedOn StoredOrders.user
    var status by StoredOrders.status
    var phone by StoredOrders.phone
    val items by StoredOrderItem referrersOn StoredOrderItems.order
    var shipmentAddress by StoredOrders.shipmentAddress
    var shipment by StoredOrders.shipment
    var shipmentDateTime by StoredOrders.shipmentDateTime
    var description by StoredOrders.description
    var userName by StoredOrders.userName
}