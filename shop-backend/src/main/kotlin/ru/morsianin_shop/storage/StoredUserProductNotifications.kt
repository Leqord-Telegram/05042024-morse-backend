package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Table

object StoredUserProductNotifications: Table("user_product_notification") {
    val user = reference("user_id", StoredUsers)
    val product = reference("product_id", StoredProducts)

    override val primaryKey = PrimaryKey(user, product)
}