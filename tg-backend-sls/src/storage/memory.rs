use crate::storage::base::*;
use crate::model::user::*;
use crate::model::product::*;
use crate::model::order::*;
use crate::model::image::*;
use crate::model::category::*;

use std::collections::HashMap;

pub struct MemoryStorage {
    users: HashMap<String, User>,
    products: HashMap<String, Product>,
    orders: HashMap<String, Order>,
    images: HashMap<String, Image>,
    categories: HashMap<String, Category>,
}

impl MemoryStorage {
    pub fn new() -> MemoryStorage {
        MemoryStorage{
            users: HashMap::new(),
            products: HashMap::new(),
            orders: HashMap::new(),
            images: HashMap::new(),
            categories: HashMap::new(),
        }
    }
}