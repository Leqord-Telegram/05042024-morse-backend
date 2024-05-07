package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.morsianin_shop.resources.Products

object StoredOrderItems: IntIdTable() {
    val product = reference("productId", StoredProducts)
    val quantity = integer("quantity")
}

class StoredOrderItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StoredOrderItem>(StoredOrderItems)
    var product by StoredProduct referencedOn StoredOrderItems.product
    var quantity by StoredOrderItems.quantity
}