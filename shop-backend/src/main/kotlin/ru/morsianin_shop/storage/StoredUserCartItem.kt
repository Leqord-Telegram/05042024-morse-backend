package ru.morsianin_shop.storage

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object StoredUserCartItems: LongIdTable("user_cart_item") {
    val user = reference("user_id", StoredUsers)
    val product = reference("product_id", StoredProducts)
    val quantity = long("quantity")
}

class StoredUserCartItem(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StoredUserCartItem>(StoredUserCartItems)
    var user by StoredUser referencedOn StoredUserCartItems.user
    var product by StoredProduct referencedOn StoredUserCartItems.product
    var quantity by StoredUserCartItems.quantity
}