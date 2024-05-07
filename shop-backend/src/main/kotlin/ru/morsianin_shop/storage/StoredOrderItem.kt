package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import ru.morsianin_shop.resources.Products

object StoredOrderItems: LongIdTable() {
    val product = reference("productId", StoredProducts)
    val quantity = integer("quantity")
}

class StoredOrderItem(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredOrderItem>(StoredOrderItems)
    var product by StoredProduct referencedOn StoredOrderItems.product
    var quantity by StoredOrderItems.quantity
}