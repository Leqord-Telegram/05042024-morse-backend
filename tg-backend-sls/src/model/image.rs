use serde::Serialize;

#[derive(Serialize, Clone, PartialEq)]
pub struct Image {
    pub id: i64,
}