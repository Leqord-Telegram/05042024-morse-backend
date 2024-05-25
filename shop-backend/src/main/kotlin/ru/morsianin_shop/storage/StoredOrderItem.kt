package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object StoredOrderItems: LongIdTable("order_item") {
    val order = reference("order_id", StoredOrders, onDelete = ReferenceOption.CASCADE)
    val product = reference("product_id", StoredProducts, onDelete = ReferenceOption.CASCADE)
    val quantity = integer("quantity")
}

class StoredOrderItem(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredOrderItem>(StoredOrderItems)
    var order by StoredOrder referencedOn StoredOrderItems.order
    var product by StoredProduct referencedOn StoredOrderItems.product
    var quantity by StoredOrderItems.quantity
}