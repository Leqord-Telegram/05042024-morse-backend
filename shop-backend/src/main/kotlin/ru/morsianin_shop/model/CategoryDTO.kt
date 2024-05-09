package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDTO(
    val id: Long,
    val name: String
)

@Serializable
data class CategoriesDTO(
    val categories: List<CategoryDTO>
)
