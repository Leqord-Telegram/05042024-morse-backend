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
    async fn get_user(&self, id: Option<i64>, admin: Option<i64>) -> Result<Vec<User>, Error> {
        let filtered_users = self.users.values()
            .filter(|&user| {
                id.map_or(true, |id_val| user.id == id_val) &&
                admin.map_or(true, |admin_val| user.admin == admin_val)
            })
            .cloned()
            .collect::<Vec<_>>();
        Ok(filtered_users)
    }

    async fn upsert_user(&mut self, user: User) -> Result<(), Error> {
        self.users.insert(user.id.to_string(), user);
        Ok(())
    }

    async fn delete_user(&mut self, id: i64) -> Result<(), Error> {
        self.users.remove(&id.to_string());
        Ok(())
    }

    async fn get_product(&self, id: Option<i64>, 
                        category_id: Option<i64>, 
                        active: Option<bool>
                    ) -> Result<Vec<Product>, Error> {
        todo!()
    }

    async fn upsert_product(&mut self, product: Product) -> Result<(), Error> {
        todo!()
    }

    async fn delete_product(&mut self, id: i64) -> Result<(), Error> {
        todo!()
    }

    async fn get_order(&self, id: Option<i64>, 
                            user_id: Option<i64>, 
                            status: Option<Status>
                        ) -> Result<Vec<Order>, Status> {
        todo!()
    }

    async fn upsert_order(&mut self, order: Order) -> Result<(), Error> {
        todo!()
    }

    async fn delete_order(&mut self, id: i64) -> Result<(), Error> {
        todo!()
    }

    async fn get_image(&self, id: Option<i64>) -> Result<Vec<Image>, Error> {
        todo!()
    }

    async fn upsert_image(&mut self, image: Image) -> Result<(), Error> {
        todo!()
    }

    async fn delete_image(&mut self, id: i64) -> Result<(), Error> {
        todo!()
    }

    async fn get_category(&self, id: Option<i64>) -> Result<Vec<Category>, Error> {
        todo!()
    }

    async fn upsert_category(&mut self, category: Category) -> Result<(), Error> {
        todo!()
    }

    async fn delete_category(&mut self, id: i64) -> Result<(), Error> {
        todo!()
    }
}