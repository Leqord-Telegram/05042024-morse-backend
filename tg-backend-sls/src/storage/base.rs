use crate::model::user::*;
use crate::model::product::*;
use crate::model::order::*;
use crate::model::image::*;
use crate::model::category::*;


pub trait Storage {
    async fn get_user(&self, filter: UserRequest) -> Result<Vec<User>, StorageError>;
    async fn upsert_user(&mut self, user: User) -> Result<(), StorageError>;
    async fn delete_user(&mut self, id: u64) -> Result<(), StorageError>;

    async fn get_product(&self, filter: ProductRequest) -> Result<Vec<Product>, StorageError>;
    async fn upsert_product(&mut self, product: Product) -> Result<(), StorageError>;
    async fn delete_product(&mut self, id: u64) -> Result<(), StorageError>;

    async fn get_order(&self, filter: OrderRequest) -> Result<Vec<Order>, StorageError>;
    async fn upsert_order(&mut self, order: Order) -> Result<(), StorageError>;
    async fn delete_order(&mut self, id: u64) -> Result<(), StorageError>;

    async fn get_image(&self, id: Option<u64>) -> Result<Vec<Image>, StorageError>;
    async fn upsert_image(&mut self, image: Image) -> Result<(), StorageError>;
    async fn delete_image(&mut self, id: u64) -> Result<(), StorageError>;

    async fn get_category(&self, id: Option<u64>) -> Result<Vec<Category>, StorageError>;
    async fn upsert_category(&mut self, category: Category) -> Result<(), StorageError>;
    async fn delete_category(&mut self, id: u64) -> Result<(), StorageError>;

    async fn get_cart(&self, user_id: Option<u64>) -> Result<Vec<Cart>, StorageError>;
    async fn upsert_cart(&mut self, cart: Cart) -> Result<(), StorageError>;
}

#[derive(Debug)]
pub enum StorageError {
    InternalError(String),
    NotFoundError(String),
    KeyCollisionError(String),
}