package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object StoredProductImages: LongIdTable("product_image") {
    val product = reference("product_id", StoredProducts, onDelete = ReferenceOption.CASCADE)
    val image = reference("image_id", StoredImages, onDelete = ReferenceOption.CASCADE)
}

class StoredProductImage(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredProductImage>(StoredProductImages)
    var product by StoredProduct referencedOn StoredProductImages.product
    var image by StoredImage referencedOn StoredProductImages.image
}