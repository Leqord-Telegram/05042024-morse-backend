use crate::storage::base::*;
use crate::model::user::*;
use crate::model::product::*;
use crate::model::order::*;
use crate::model::image::*;
use crate::model::category::*;

use std::collections::HashMap;

pub struct MemoryStorage {
    users: HashMap<String, User>,
    carts: HashMap<String, Cart>,
    products: HashMap<String, Product>,
    orders: HashMap<String, Order>,
    images: HashMap<String, Image>,
    categories: HashMap<String, Category>,
}

impl MemoryStorage {
    pub fn new() -> MemoryStorage {
        MemoryStorage{
            users: HashMap::new(),
            carts: HashMap::new(),
            products: HashMap::new(),
            orders: HashMap::new(),
            images: HashMap::new(),
            categories: HashMap::new(),
        }
    }
}

impl Storage for MemoryStorage {
    async fn get_user(&self, filter: UserRequest) -> Result<Vec<User>, StorageError> {
        let filtered_users = self.users.values()
            .filter(|&user| {
                filter.id.map_or(true, |id_val| user.id == id_val) &&
                filter.admin.map_or(true, |admin_val| user.admin == admin_val) &&
                filter.name.as_ref().map_or(true, |name_val| user.name == *name_val)
            })
            .cloned()
            .collect::<Vec<_>>();
        Ok(filtered_users)
    }

    async fn upsert_user(&mut self, user: User) -> Result<(), StorageError> {
        self.users.insert(user.id.to_string(), user);
        Ok(())
    }

    async fn delete_user(&mut self, id: u64) -> Result<(), StorageError> {
        self.users.remove(&id.to_string());
        Ok(())
    }

    async fn get_product(&self, filter: ProductRequest) -> Result<Vec<Product>, StorageError> {
        let products = self.products.values()
            .filter(|product| 
                filter.id.map_or(true, |id_val| product.id == id_val) &&
                filter.category_id.map_or(true, |cat_id| product.category_id == cat_id) &&
                filter.active.map_or(true, |active_val| product.active == active_val) &&
                filter.name.as_ref().map_or(true, |name_val| product.name == *name_val) &&
                filter.description.as_ref().map_or(true, |description_val| product.description == *description_val) &&
                filter.price.map_or(true, |price_val| product.price == price_val) &&
                filter.quantity.map_or(true, |quantity_val| product.quantity == quantity_val)
            )
            .cloned()
            .collect::<Vec<_>>();
        Ok(products)
    }

    async fn upsert_product(&mut self, product: Product) -> Result<(), StorageError> {
        self.products.insert(product.id.to_string(), product);
        Ok(())
    }

    async fn delete_product(&mut self, id: u64) -> Result<(), StorageError> {
        self.products.remove(&id.to_string()).map_or_else(
            || Err(StorageError::NotFoundError(format!("Product with ID {} not found", id))),
            |_| Ok(())
        )
    }

    async fn get_cart(&self, user_id: Option<i64>) -> Result<Vec<Cart>, StorageError> {
        let carts = self.carts.values()
        .filter(|cart| 
            user_id.map_or(true, |user_id| cart.user_id == user_id))
        .cloned()
        .collect::<Vec<_>>();
    Ok(carts)
    }

    async fn upsert_cart(&mut self, cart: Cart) -> Result<(), StorageError> {
        self.carts.insert(cart.user_id.to_string(), cart);
        Ok(())
    }

    async fn get_order(&self, filter: OrderRequest) -> Result<Vec<Order>, StorageError> {
        let orders = self.orders.values()
            .filter(|order| 
                filter.id.map_or(true, |id_val| order.id == id_val) &&
                filter.user_id.map_or(true, |user_id_val| order.user_id == user_id_val) &&
                filter.status.as_ref().map_or(true, |status_val| order.status == *status_val)
            )
            .cloned()
            .collect::<Vec<_>>();
        Ok(orders)
    }

    async fn upsert_order(&mut self, order: Order) -> Result<(), StorageError> {
        self.orders.insert(order.id.to_string(), order);
        Ok(())
    }

    async fn delete_order(&mut self, id: u64) -> Result<(), StorageError> {
        self.orders.remove(&id.to_string()).map_or_else(
            || Err(StorageError::NotFoundError(format!("Order with ID {} not found", id))),
            |_| Ok(())
        )
    }

    async fn get_image(&self, id: Option<u64>) -> Result<Vec<Image>, StorageError> {
        let images = self.images.values()
            .filter(|image| 
                id.map_or(true, |id_val| image.id == id_val)
            )
            .cloned()
            .collect::<Vec<_>>();
        Ok(images)
    }

    async fn upsert_image(&mut self, image: Image) -> Result<(), StorageError> {
        self.images.insert(image.id.to_string(), image);
        Ok(())
    }

    async fn delete_image(&mut self, id: u64) -> Result<(), StorageError> {
        self.images.remove(&id.to_string()).map_or_else(
            || Err(StorageError::NotFoundError(format!("Image with ID {} not found", id))),
            |_| Ok(())
        )
    }
    async fn get_category(&self, id: Option<u64>) -> Result<Vec<Category>, StorageError> {
        let categories = self.categories.values()
            .filter(|category| 
                id.map_or(true, |id_val| category.id == id_val)
            )
            .cloned()
            .collect::<Vec<_>>();
        Ok(categories)
    }

    async fn upsert_category(&mut self, category: Category) -> Result<(), StorageError> {
        self.categories.insert(category.id.to_string(), category);
        Ok(())
    }

    async fn delete_category(&mut self, id: u64) -> Result<(), StorageError> {
        self.categories.remove(&id.to_string()).map_or_else(
            || Err(StorageError::NotFoundError(format!("Category with ID {} not found", id))),
            |_| Ok(())
        )
    }
}