use serde::Serialize;

#[derive(Serialize)]
pub struct Product {
    pub id: i64,
    pub name: String,
    pub destription: String,
    pub price: f64,
    pub quantity: i64
}