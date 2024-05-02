use crate::model::user::*;
use crate::model::product::*;
use crate::model::order::*;
use crate::model::image::*;
use crate::model::category::*;


pub trait Storage {
    async fn get_user(&self, id: Option<i64>, admin: Option<bool>) -> Result<Vec<User>, Error>;
    async fn upsert_user(&mut self, user: User) -> Result<(), Error>;
    async fn delete_user(&mut self, id: i64) -> Result<(), Error>;

    async fn get_product(&self, id: Option<i64>, category_id: Option<i64>, active: Option<bool>) -> Result<Vec<Product>, Error>;
    async fn upsert_product(&mut self, product: Product) -> Result<(), Error>;
    async fn delete_product(&mut self, id: i64) -> Result<(), Error>;

    async fn get_order(&self, id: Option<i64>, user_id: Option<i64>, status: Option<Status>) -> Result<Vec<Order>, Status>;
    async fn upsert_order(&mut self, order: Order) -> Result<(), Error>;
    async fn delete_order(&mut self, id: i64) -> Result<(), Error>;

    async fn get_image(&self, id: Option<i64>) -> Result<Vec<Image>, Error>;
    async fn upsert_image(&mut self, image: Image) -> Result<(), Error>;
    async fn delete_image(&mut self, id: i64) -> Result<(), Error>;

    async fn get_category(&self, id: Option<i64>) -> Result<Vec<Category>, Error>;
    async fn upsert_category(&mut self, category: Category) -> Result<(), Error>;
    async fn delete_category(&mut self, id: i64) -> Result<(), Error>;
}

pub enum Error {
    InternalError(String),
    NotFoundError(String),
}