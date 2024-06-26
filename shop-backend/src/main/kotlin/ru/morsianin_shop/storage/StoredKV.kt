package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table

object StoredKVs : LongIdTable("kv_store") {
    val value = text("value")
}

class StoredKV(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredKV>(StoredKVs)
    var value by StoredKVs.value
}