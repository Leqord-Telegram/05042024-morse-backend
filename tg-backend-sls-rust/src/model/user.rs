use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct User {
    pub id: i64,
    pub name: String,
    pub admin: bool,
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct UserRequest {
    pub id: Option<i64>,
    pub name: Option<String>,
    pub admin: Option<bool>,
}