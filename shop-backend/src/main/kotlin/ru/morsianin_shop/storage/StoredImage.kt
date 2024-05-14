package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import ru.morsianin_shop.model.ImageFormat

object StoredImages: LongIdTable("image") {
    val storedId = text("storage_id").uniqueIndex()
    val format = enumerationByName<ImageFormat>("format", 255)
}

class StoredImage(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredImage>(StoredImages)
    var storedId by StoredImages.storedId
    var format by StoredImages.format
}