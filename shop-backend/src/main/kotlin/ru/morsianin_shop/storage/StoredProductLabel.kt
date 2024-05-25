package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object StoredProductLabels: LongIdTable("product_label") {
    val product = reference("product_id", StoredProducts, onDelete = ReferenceOption.CASCADE)
    val label = reference("label_id", StoredLabels, onDelete = ReferenceOption.CASCADE)
}

class StoredProductLabel(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredProductLabel>(StoredProductLabels)
    var product by StoredProduct referencedOn StoredProductLabels.product
    var label by StoredLabel referencedOn StoredProductLabels.label
}