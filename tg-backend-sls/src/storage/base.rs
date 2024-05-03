use crate::model::user::*;
use crate::model::product::*;
use crate::model::order::*;
use crate::model::image::*;
use crate::model::category::*;


pub trait Storage {
    async fn get_user(&self, id: Option<i64>, admin: Option<bool>) -> Result<Vec<User>, StorageError>;
    async fn upsert_user(&mut self, user: User) -> Result<(), StorageError>;
    async fn delete_user(&mut self, id: i64) -> Result<(), StorageError>;

    async fn get_product(&self, id: Option<i64>, category_id: Option<i64>, active: Option<bool>) -> Result<Vec<Product>, StorageError>;
    async fn upsert_product(&mut self, product: Product) -> Result<(), StorageError>;
    async fn delete_product(&mut self, id: i64) -> Result<(), StorageError>;

    async fn get_order(&self, id: Option<i64>, user_id: Option<i64>, status: Option<Status>) -> Result<Vec<Order>, StorageError>;
    async fn upsert_order(&mut self, order: Order) -> Result<(), StorageError>;
    async fn delete_order(&mut self, id: i64) -> Result<(), StorageError>;

    async fn get_image(&self, id: Option<i64>) -> Result<Vec<Image>, StorageError>;
    async fn upsert_image(&mut self, image: Image) -> Result<(), StorageError>;
    async fn delete_image(&mut self, id: i64) -> Result<(), StorageError>;

    async fn get_category(&self, id: Option<i64>) -> Result<Vec<Category>, StorageError>;
    async fn upsert_category(&mut self, category: Category) -> Result<(), StorageError>;
    async fn delete_category(&mut self, id: i64) -> Result<(), StorageError>;

    async fn get_cart(&self, user_id: Option<i64>) -> Result<Vec<Cart>, StorageError>;
    async fn upsert_cart(&mut self, cart: Cart) -> Result<(), StorageError>;
}

pub enum StorageError {
    InternalError(String),
    NotFoundError(String),
}