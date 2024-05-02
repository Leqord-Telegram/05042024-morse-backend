use serde::Serialize;

#[derive(Serialize, Clone, PartialEq)]
pub struct User {
    pub id: i64,
    pub name: String,
    pub admin: bool,
}