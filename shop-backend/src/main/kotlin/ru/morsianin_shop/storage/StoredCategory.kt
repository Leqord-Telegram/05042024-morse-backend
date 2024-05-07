package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredCategories: LongIdTable() {
    val name = text("name")
}

class StoredCategory(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredCategory>(StoredCategories)

    var name by StoredCategories.name
}
