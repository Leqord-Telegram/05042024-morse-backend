package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import ru.morsianin_shop.storage.StoredProducts.default
import ru.morsianin_shop.storage.StoredProducts.text

object StoredProducts: LongIdTable("product") {
    val name = text("name")
    val description = text("description").nullable().default("")
    val price = long("price")
    val priceOld = long("price_old").nullable()
    val quantity = long("quantity")
    val active = bool("active")
    val inStock = bool("in_stock").default(true)
    val shipmentScheduledAt = date("shipment_scheduled_at").nullable()
    val createdAt = date("created_date")
    val enabled = bool("enabled").default(true)
    val unit = text("unit").default("")
}

class StoredProduct(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredProduct>(StoredProducts)
    var name by StoredProducts.name
    var description by StoredProducts.description
    var categories by StoredCategory via StoredProductCategories
    var price by StoredProducts.price
    var priceOld by StoredProducts.priceOld
    var quantity by StoredProducts.quantity
    var active by StoredProducts.active
    var enabled by StoredProducts.enabled
    var createdAt by StoredProducts.createdAt
    var images by StoredImage via StoredProductImages
    var labels by StoredLabel via StoredProductLabels
    var inStock by StoredProducts.inStock
    var shipmentScheduledAt by StoredProducts.shipmentScheduledAt
    var unit by StoredProducts.unit
}