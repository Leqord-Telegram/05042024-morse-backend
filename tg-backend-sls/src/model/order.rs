use serde::Serialize;

use super::product::Product;

#[derive(Serialize)]
pub struct Order {
    pub id: i64,
    pub items: Vec<OrderItem>,
}

#[derive(Serialize)]
pub struct OrderItem {
    pub id: i64,
    pub product: Product,
    pub quantity: u64,
}