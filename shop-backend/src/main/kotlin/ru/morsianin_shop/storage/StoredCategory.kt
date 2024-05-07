package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object StoredCategories: IntIdTable() {
    val name = text("name")
}

class StoredCategory(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StoredCategory>(StoredCategories)

    var name by StoredCategories.name
}
