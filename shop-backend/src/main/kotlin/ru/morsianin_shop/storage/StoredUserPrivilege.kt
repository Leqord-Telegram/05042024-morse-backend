package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import ru.morsianin_shop.model.UserDTO
import ru.morsianin_shop.model.UserPrivilegeDTO

object StoredUserPrivileges: LongIdTable("user_privilege") {
    val user = reference("user_id", StoredUsers)
    val privilege = enumerationByName<UserPrivilegeDTO>("privilege", 255)
}

class StoredUserPrivilege(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredUser>(StoredUsers)
    var user by StoredUser referencedOn StoredUsers.id
    var privilege by StoredUserPrivileges.privilege
}