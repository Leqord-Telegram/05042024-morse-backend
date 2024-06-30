package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class TelegramAuthUserData(
    val id: Long,
)

@Serializable
enum class AuthType {
    Telegram,
}