package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredUsers: LongIdTable("user") {
    val name = text("name").nullable().uniqueIndex()
    val tgId = long("tg_id").nullable().uniqueIndex()
    val fullName = text("full_name").nullable()
    val phone = text("phone").nullable()
}

class StoredUser(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredUser>(StoredUsers)
    var name by StoredUsers.name
    var tgId by StoredUsers.tgId
    val cartItems by StoredUserCartItem referrersOn StoredUserCartItems.user
    val privileges by StoredUserPrivilege referrersOn StoredUserPrivileges.user
    var fullName by StoredUsers.fullName
    var phone by StoredUsers.phone
}