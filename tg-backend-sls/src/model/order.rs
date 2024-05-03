use serde::{Deserialize, Serialize};

// надо было отдельно выделять Filter и CreateRequest или какой-то макрос, где поля опциональны

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct Cart {
    pub user_id: i64,
    pub items: Vec<OrderItem>,
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct Order {
    pub id: u64,
    pub user_id: i64,
    pub items: Vec<OrderItem>,
    pub status: Status
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct OrderRequest {
    pub id: Option<u64>,
    pub user_id: Option<i64>,
    pub items: Option<Vec<OrderItemRequest>>,
    pub status: Option<Status>,
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct OrderItem {
    pub product_id: u64,
    pub quantity: u64,
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct OrderItemRequest {
    pub product_id: Option<u64>,
    pub quantity: Option<u64>,
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub enum Status {
    Failed,
    Pending,
    Shipping,
    Arrived,
    Finished
}