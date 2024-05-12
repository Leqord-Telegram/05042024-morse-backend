package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderItemResponse
import ru.morsianin_shop.model.OrderStatus
import ru.morsianin_shop.model.UserPrivilege

@Resource("/user")
class UserRequest(
    val name: String? = null,
    //val admin: Boolean? = null,
    //val privileges: UserPrivilege?,
) {
    @Resource("{id}")
    class Id(val parent: UserRequest = UserRequest(), val id: Long)

    @Resource("me")
    class Me(val parent: UserRequest = UserRequest())
}