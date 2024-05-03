mod model;
mod storage;

use model::category::Category;
use model::product::Product;
use storage::memory::*;
use storage::base::*;

use std::env;
use std::sync::Arc;
use actix_web::{get, post, web, App, HttpResponse, HttpServer, Responder};
use serde::Deserialize;
use serde_json::json;
use tokio::sync::Mutex;


#[derive(Deserialize)]
struct ProductQuery {
    id: Option<i64>,
    category_id: Option<i64>,
    active: Option<bool>,
}

struct AppState {
    storage: Arc<Mutex<MemoryStorage>>,
}

impl AppState {
    pub async fn new() -> Self {
        let state = AppState {
            storage: Arc::new(Mutex::new(MemoryStorage::new())),
        };

        return state;
    }
}


#[get("/products")]
async fn get_products(
    data: web::Data<AppState>,
    query: web::Query<ProductQuery>
) -> impl Responder {
    let storage = data.storage.lock().await;

    let query_info = query.into_inner();
    let products = storage.get_product(query_info.id, 
                                                            query_info.category_id, 
                                                            query_info.active
                                                        ).await.unwrap_or_else(|_| vec![]);
    HttpResponse::Ok().json(products)
}

#[post("/products")]
async fn create_product(
    data: web::Data<AppState>,
    product: web::Json<Product>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    match storage.upsert_product(product.into_inner()).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) =>
            match e {
                StorageError::InternalError(s) => HttpResponse::InternalServerError().json(json!({"status": "error", "details": s})),
                StorageError::NotFoundError(s) => HttpResponse::InternalServerError().json(json!({"status": "not found", "details": s})),
            }
        
    }
}

#[get("/categories")]
async fn get_categories(data: web::Data<AppState>) -> impl Responder {
    let storage = data.storage.lock().await;

    let categories = storage.get_category(None).await.unwrap_or_else(|_| vec![]);
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