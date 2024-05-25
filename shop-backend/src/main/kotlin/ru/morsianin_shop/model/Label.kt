package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class LabelResponse(
    val id: Long,
    val name: String,
    val color: LabelColorRGB
)

@Serializable
data class LabelColorRGB (
    val red: UByte,
    val green: UByte,
    val blue: UByte,
)