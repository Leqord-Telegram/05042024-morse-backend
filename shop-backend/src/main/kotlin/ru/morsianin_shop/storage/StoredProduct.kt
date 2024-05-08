package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredProducts: LongIdTable() {
    val name = text("name")
    val description = text("description")
    val category = reference("category_id", Categories)
    val price = integer("price")
    val quantity = integer("quantity")
    val active = bool("active")
}

class StoredProduct(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredProduct>(StoredProducts)
    var name by StoredProducts.name
    var description by StoredProducts.description
    var category by StoredCategory referencedOn StoredProducts.category
    var price by StoredProducts.price
    var quantity by StoredProducts.quantity
    var active by StoredProducts.active
}