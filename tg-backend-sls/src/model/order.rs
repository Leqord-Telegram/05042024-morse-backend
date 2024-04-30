use serde::Serialize;

#[derive(Serialize)]
pub struct Order {
    pub id: i64,
    pub user_id: i64,
    pub items: Vec<Item>,
    pub status: Status
}

#[derive(Serialize)]
pub struct Item {
    pub id: i64,
    pub product_id: i64,
    pub quantity: u64,
}

#[derive(Serialize)]
pub enum Status {
    Failed,
    Pending,
    Shipping,
    Arrived,
    Finished
}