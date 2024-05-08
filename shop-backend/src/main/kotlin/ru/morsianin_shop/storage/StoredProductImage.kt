package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredProductImages: LongIdTable("product_image") {
    val product = reference("product_id", StoredProducts)
    val image = reference("image_id", StoredImages)
}

class StoredProductImage(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredProductImage>(StoredProductImages)
    var product by StoredProduct referencedOn StoredProductImages.product
    var image by StoredImage referencedOn StoredProductImages.image
}