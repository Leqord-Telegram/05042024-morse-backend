package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object StoredUsers: IntIdTable() {
    val name = text("name")
    val username = text("username")
}

class StoredUser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StoredUser>(StoredUsers)
    var name by StoredUsers.name
    var username by StoredUsers.username
}