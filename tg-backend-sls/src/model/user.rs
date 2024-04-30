use serde::Serialize;

#[derive(Serialize)]
pub struct User {
    pub id: i64,
    pub name: String,
    pub admin: bool
}