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
    async fn get_user(&self, id: Option<i64>, admin: Option<bool>) -> Result<Vec<User>, Error> {
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

    async fn get_product(&self, id: Option<i64>, category_id: Option<i64>, active: Option<bool>) -> Result<Vec<Product>, Error> {
        let products = self.products.values()
            .filter(|product| 
                id.map_or(true, |id_val| product.id == id_val) &&
                category_id.map_or(true, |cat_id| product.category_id == cat_id) &&
                active.map_or(true, |active_val| product.active == active_val)
            )
            .cloned()
            .collect::<Vec<_>>();
        Ok(products)
    }

    async fn upsert_product(&mut self, product: Product) -> Result<(), Error> {
        self.products.insert(product.id.to_string(), product);
        Ok(())
    }

    async fn delete_product(&mut self, id: i64) -> Result<(), Error> {
        self.products.remove(&id.to_string()).map_or_else(
            || Err(Error::NotFoundError(format!("Product with ID {} not found", id))),
            |_| Ok(())
        )
    }

    // Методы для заказов
    async fn get_order(&self, id: Option<i64>, user_id: Option<i64>, status: Option<Status>) -> Result<Vec<Order>, Error> {
        let orders = self.orders.values()
            .filter(|order| 
                id.map_or(true, |id_val| order.id == id_val) &&
                user_id.map_or(true, |user_id_val| order.user_id == user_id_val) &&
                status.as_ref().map_or(true, |status_val| order.status == *status_val)
            )
            .cloned()
            .collect::<Vec<_>>();
        Ok(orders)
    }

    async fn upsert_order(&mut self, order: Order) -> Result<(), Error> {
        self.orders.insert(order.id.to_string(), order);
        Ok(())
    }

    async fn delete_order(&mut self, id: i64) -> Result<(), Error> {
        self.orders.remove(&id.to_string()).map_or_else(
            || Err(Error::NotFoundError(format!("Order with ID {} not found", id))),
            |_| Ok(())
        )
    }

    // Методы для изображений
    async fn get_image(&self, id: Option<i64>) -> Result<Vec<Image>, Error> {
        let images = self.images.values()
            .filter(|image| 
                id.map_or(true, |id_val| image.id == id_val)
            )
            .cloned()
            .collect::<Vec<_>>();
        Ok(images)
    }

    async fn upsert_image(&mut self, image: Image) -> Result<(), Error> {
        self.images.insert(image.id.to_string(), image);
        Ok(())
    }

    async fn delete_image(&mut self, id: i64) -> Result<(), Error> {
        self.images.remove(&id.to_string()).map_or_else(
            || Err(Error::NotFoundError(format!("Image with ID {} not found", id))),
            |_| Ok(())
        )
    }

    // Методы для категорий
    async fn get_category(&self, id: Option<i64>) -> Result<Vec<Category>, Error> {
        let categories = self.categories.values()
            .filter(|category| 
                id.map_or(true, |id_val| category.id == id_val)
            )
            .cloned()
            .collect::<Vec<_>>();
        Ok(categories)
    }

    async fn upsert_category(&mut self, category: Category) -> Result<(), Error> {
        self.categories.insert(category.id.to_string(), category);
        Ok(())
    }

    async fn delete_category(&mut self, id: i64) -> Result<(), Error> {
        self.categories.remove(&id.to_string()).map_or_else(
            || Err(Error::NotFoundError(format!("Category with ID {} not found", id))),
            |_| Ok(())
        )
    }
}