use serde::Serialize;

#[derive(Serialize, Clone, PartialEq)]
pub struct Category {
    pub id: i64,
    pub name: String
}