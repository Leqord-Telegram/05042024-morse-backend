package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredImages: LongIdTable() {
    val url = text("url")
}

class StoredImage(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredImage>(StoredImages)
    var url by StoredImages.url
}