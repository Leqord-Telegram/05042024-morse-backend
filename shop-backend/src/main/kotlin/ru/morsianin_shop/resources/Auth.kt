package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.AuthType
import ru.morsianin_shop.model.UserPrivilege

@Resource("/login")
data class AuthRequest(
    val privilege: UserPrivilege? = null,
    val type: AuthType = AuthType.Telegram
)