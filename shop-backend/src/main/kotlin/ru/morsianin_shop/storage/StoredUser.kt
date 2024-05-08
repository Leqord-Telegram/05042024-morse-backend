package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredUsers: LongIdTable("user") {
    val name = text("name")
    val tgId = long("tg_id").nullable().uniqueIndex()
    val tgUsername = text("tg_username").nullable()
}

class StoredUser(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredUser>(StoredUsers)
    var name by StoredUsers.name
    val tgId by StoredUsers.tgId
    var tgUsername by StoredUsers.tgUsername
}