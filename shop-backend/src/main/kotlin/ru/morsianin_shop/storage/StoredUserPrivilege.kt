package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import ru.morsianin_shop.model.UserPrivilege

object StoredUserPrivileges: LongIdTable("user_privilege") {
    val user = reference("user_id", StoredUsers)
    val privilege = enumerationByName<UserPrivilege>("privilege", 255)
}

class StoredUserPrivilege(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredUserPrivilege>(StoredUserPrivileges)
    var user by StoredUser referencedOn StoredUserPrivileges.user
    var privilege by StoredUserPrivileges.privilege
}