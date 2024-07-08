package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import ru.morsianin_shop.storage.StoredProducts.default

object StoredCategories: LongIdTable("category") {
    val name = text("name")
    val enabled = bool("enabled").default(true)
    val orderPriority = long("order_priority").nullable()
}

class StoredCategory(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredCategory>(StoredCategories)
    var enabled by StoredCategories.enabled
    var name by StoredCategories.name
    var orderPriority by StoredCategories.orderPriority
}
