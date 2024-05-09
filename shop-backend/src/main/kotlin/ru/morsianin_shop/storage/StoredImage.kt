package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredImages: LongIdTable("image") {
    val url = text("url")
    val blob = blob("content").nullable()
}

class StoredImage(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredImage>(StoredImages)
    var url by StoredImages.url
    var blob by StoredImages.blob
}