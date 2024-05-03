use serde::{Serialize,  Deserialize};

#[derive(Serialize, Deserialize, Clone, PartialEq)]
pub struct Image {
    pub id: i64,
}