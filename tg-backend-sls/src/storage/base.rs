use crate::model::user::*;
use crate::model::product::*;
use crate::model::order::*;
use crate::model::image::*;
use crate::model::category::*;


pub trait Storage {
    async fn get_user(&self, id: Option<i64>, admin: Option<i64>) -> Result<User, Error>;
    async fn upsert_user(&self, user: User) -> Result<(), Error>;
    async fn delete_user(&self, id: i64) -> Result<(), Error>;

    async fn get_product(&self, id: Option<i64>, category_id: Option<i64>, active: Option<bool>) -> Result<Product, Error>;
    async fn upsert_product(&self, product: Product) -> Result<(), Error>;
    async fn delete_product(&self, id: i64) -> Result<(), Error>;

    async fn get_order(&self, id: Option<i64>, user_id: Option<i64>, status: Option<Status>) -> Result<Order, Status>;
    async fn upsert_order(&self, order: Order) -> Result<(), Error>;
    async fn delete_order(&self, id: i64) -> Result<(), Error>;

    async fn get_image(&self, id: Option<i64>) -> Result<Image, Error>;
    async fn upsert_image(&self, image: Image) -> Result<(), Error>;
    async fn delete_image(&self, id: i64) -> Result<(), Error>;

    async fn get_category(&self, id: Option<i64>) -> Result<Category, Error>;
    async fn upsert_category(&self, category: Category) -> Result<(), Error>;
    async fn delete_category(&self, id: i64) -> Result<(), Error>;
}

pub enum Error {
    NotFound(String),
    MultipleMatches(String),
    InternalError(String),
}