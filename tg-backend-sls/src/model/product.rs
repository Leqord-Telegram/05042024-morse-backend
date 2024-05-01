use serde::Serialize;

use super::image::Image;

#[derive(Serialize)]
pub struct Product {
    pub id: i64,
    pub name: String,
    pub destription: String,
    pub category_id: i64,
    pub price: i64,
    pub quantity: i64,
    pub active: bool,
    pub images: Vec<Image>
}