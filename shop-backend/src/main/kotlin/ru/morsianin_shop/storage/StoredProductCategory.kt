package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object StoredProductCategories: LongIdTable("product_category") {
    val product = reference("product_id", StoredProducts, onDelete = ReferenceOption.CASCADE)
    val category = reference("category_id", StoredCategories, onDelete = ReferenceOption.CASCADE)
}

class StoredProductCategory(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredProductCategory>(StoredProductCategories)
    var product by StoredProduct referencedOn StoredProductCategories.product
    var category by StoredCategory referencedOn StoredProductCategories.category
}