use serde::{Deserialize, Serialize};

use super::image::Image;

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct Product {
    pub id: i64,
    pub name: String,
    pub description: String,
    pub category_id: i64,
    pub price: i64,
    pub quantity: i64,
    pub active: bool,
    pub images: Vec<Image>,
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct ProductRequest {
    pub id: Option<i64>,
    pub name: Option<String>,
    pub description: Option<String>,
    pub category_id: Option<i64>,
    pub price: Option<i64>,
    pub quantity: Option<i64>,
    pub active: Option<bool>,
    pub images: Option<Vec<Image>>,
}