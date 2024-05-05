use serde::{Deserialize, Serialize};

use super::image::Image;

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct Product {
    pub id: u64,
    pub name: String,
    pub description: String,
    pub category_id: u64,
    pub price: u64,
    pub quantity: u64,
    pub active: bool,
    pub images: Vec<Image>,
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct ProductRequest {
    pub id: Option<u64>,
    pub name: Option<String>,
    pub description: Option<String>,
    pub category_id: Option<u64>,
    pub price: Option<u64>,
    pub quantity: Option<u64>,
    pub active: Option<bool>,
    pub images: Option<Vec<Image>>,
}