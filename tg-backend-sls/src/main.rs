mod model;

mod storage;
use model::category::Category;
use model::product::Product;
use storage::memory::*;
use storage::base::*;

use std::env;
use actix_web::{get, post, web, App, HttpResponse, HttpServer, Responder};

struct AppState {
    storage: MemoryStorage,
}

impl AppState {
    pub async fn new() -> Self {
        let mut state = AppState {
            storage: MemoryStorage::new(),
        };

        let _ = state.storage.upsert_category(Category{
            id: 123,
            name: "Shit".to_string(),
        }).await;

        let _ = state.storage.upsert_category(Category{
            id: 141,
            name: "Show".to_string(),
        }).await;

        let _ = state.storage.upsert_product(Product{
            id: 6123,
            name: "Shit".to_string(),
            destription: "Huita".to_string(),
            category_id: 123,
            price: 5123,
            quantity: 22,
            active: true,
            images: vec![],
        }).await;

        return state;
    }
}

#[get("/products")]
async fn get_products(data: web::Data<AppState>) -> impl Responder {
    let products = data.storage.get_product(None, None, None).await.unwrap_or_else(|_| vec![]);
    HttpResponse::Ok().json(products)
}

#[get("/categories")]
async fn get_categories(data: web::Data<AppState>) -> impl Responder {
    let categories = data.storage.get_category(None).await.unwrap_or_else(|_| vec![]);
    HttpResponse::Ok().json(categories)
}

#[post("/echo")]
async fn echo(req_body: String) -> impl Responder {
    HttpResponse::Ok().body(req_body)
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let host = env::var("HOST").unwrap_or_else(|_| "127.0.0.1".to_string());
    let port = env::var("PORT").unwrap_or_else(|_| "8080".to_string());

    let app_state = web::Data::new(AppState::new().await);

    println!("Listening on {}:{}", host, port);

    HttpServer::new(move || {
        App::new()
            .app_data(app_state.clone())
            .service(echo)
            .service(get_categories)
            .service(get_products)
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}