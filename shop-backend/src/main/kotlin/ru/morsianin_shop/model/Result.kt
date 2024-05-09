package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class ResultCreated(
    val id: Long
)

// TODO: возвращать его после всех операций создания