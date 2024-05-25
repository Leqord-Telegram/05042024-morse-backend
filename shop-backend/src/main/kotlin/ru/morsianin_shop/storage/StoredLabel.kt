package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredLabels : LongIdTable("label") {
    val name = text("name")
    val colorRed = short("color_red")
    val colorGreen = short("color_green")
    val colorBlue = short("color_blue")
}

class StoredLabel(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredLabel>(StoredLabels)
    var name by StoredLabels.name
    var colorRed by StoredLabels.colorRed
    var colorGreen by StoredLabels.colorGreen
    var colorBlue by StoredLabels.colorBlue
}