package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object StoredImages: IntIdTable() {
    val url = text("url")
}

class StoredImage(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StoredImage>(StoredImages)
    var url by StoredImages.url
}