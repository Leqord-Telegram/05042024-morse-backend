package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object StoredProducts: IntIdTable() {
    val name = text("name")
    val description = text("description")
    val category = reference("category", StoredCategories)
    val price = integer("price")
    val quantity = integer("quantity")
    val active = bool("active")
}

class StoredProduct(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StoredProduct>(StoredProducts)
    var name by StoredProducts.name
    var description by StoredProducts.description
    var category by StoredCategory referencedOn StoredProducts.category
    var price by StoredProducts.price
    var quantity by StoredProducts.quantity
    var active by StoredProducts.active
}