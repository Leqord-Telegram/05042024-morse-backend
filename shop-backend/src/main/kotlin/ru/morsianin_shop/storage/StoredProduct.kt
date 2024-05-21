package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredProducts: LongIdTable("product") {
    val name = text("name")
    val description = text("description").nullable().default("")
    val category = reference("category_id", StoredCategories)
    val price = long("price")
    val priceOld = long("price_old").nullable()
    val quantity = long("quantity")
    val active = bool("active")
    val enabled = bool("enabled").default(true)
}

class StoredProduct(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredProduct>(StoredProducts)
    var name by StoredProducts.name
    var description by StoredProducts.description
    var category by StoredCategory referencedOn StoredProducts.category
    var price by StoredProducts.price
    var priceOld by StoredProducts.priceOld
    var quantity by StoredProducts.quantity
    var active by StoredProducts.active
    var enabled by StoredProducts.enabled
    var images by StoredImage via StoredProductImages
}