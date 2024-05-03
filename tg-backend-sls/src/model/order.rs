use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct Cart {
    pub user_id: u64,
    pub items: Vec<Item>,
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct Order {
    pub id: u64,
    pub user_id: u64,
    pub items: Vec<Item>,
    pub status: Status
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct OrderRequest {
    pub id: Option<u64>,
    pub user_id: Option<u64>,
    pub items: Option<Vec<ItemRequest>>,
    pub status: Option<Status>,
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct Item {
    pub id: u64,
    pub product_id: u64,
    pub quantity: u64,
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct ItemRequest {
    pub id: Option<u64>,
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