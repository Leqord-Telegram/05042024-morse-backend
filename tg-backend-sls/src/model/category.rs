use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct Category {
    pub id: i64,
    pub name: String
}

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct CategoryRequest {
    pub id: Option<i64>,
    pub name: Option<String>
}
