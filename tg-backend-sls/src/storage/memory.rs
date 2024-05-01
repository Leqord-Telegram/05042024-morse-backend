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

impl Storage for MemoryStorage {
    async fn get_user(&self, id: Option<i64>, admin: Option<i64>) -> Result<User, Error> {
        todo!()
    }

    async fn upsert_user(&self, user: User) -> Result<(), Error> {
        todo!()
    }

    async fn delete_user(&self, id: i64) -> Result<(), Error> {
        todo!()
    }

    async fn get_product(&self, id: Option<i64>, category_id: Option<i64>, active: Option<bool>) -> Result<Product, Error> {
        todo!()
    }

    async fn upsert_product(&self, product: Product) -> Result<(), Error> {
        todo!()
    }

    async fn delete_product(&self, id: i64) -> Result<(), Error> {
        todo!()
    }

    async fn get_order(&self, id: Option<i64>, user_id: Option<i64>, status: Option<Status>) -> Result<Order, Status> {
        todo!()
    }

    async fn upsert_order(&self, order: Order) -> Result<(), Error> {
        todo!()
    }

    async fn delete_order(&self, id: i64) -> Result<(), Error> {
        todo!()
    }

    async fn get_image(&self, id: Option<i64>) -> Result<Image, Error> {
        todo!()
    }

    async fn upsert_image(&self, image: Image) -> Result<(), Error> {
        todo!()
    }

    async fn delete_image(&self, id: i64) -> Result<(), Error> {
        todo!()
    }

    async fn get_category(&self, id: Option<i64>) -> Result<Category, Error> {
        todo!()
    }

    async fn upsert_category(&self, category: Category) -> Result<(), Error> {
        todo!()
    }

    async fn delete_category(&self, id: i64) -> Result<(), Error> {
        todo!()
    }
}