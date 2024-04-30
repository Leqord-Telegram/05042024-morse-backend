use serde::Serialize;

#[derive(Serialize)]
pub struct Category {
    pub id: i64,
    pub name: String
}