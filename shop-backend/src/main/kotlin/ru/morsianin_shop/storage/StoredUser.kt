package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredUsers: LongIdTable() {
    val name = text("name")
    val username = text("username")
}

class StoredUser(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredUser>(StoredUsers)
    var name by StoredUsers.name
    var username by StoredUsers.username
}