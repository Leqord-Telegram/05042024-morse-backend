package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Categories: LongIdTable() {
    val name = text("name")
}

class StoredCategory(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredCategory>(Categories)

    var name by Categories.name
}
