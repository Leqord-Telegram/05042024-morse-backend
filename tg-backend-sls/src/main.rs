mod model;
mod storage;

use model::category::*;
use model::product::*;
use storage::memory::*;
use storage::base::*;

use std::env;
use std::sync::Arc;
use actix_web::{get, post, web, App, HttpResponse, HttpServer, Responder};
use serde_json::json;
use tokio::sync::Mutex;
use chrono::{Utc, TimeZone};
use std::collections::hash_map::DefaultHasher;
use std::hash::{Hash, Hasher};

// Функция для генерации идентификатора на основе текущего времени и строки текста
fn generate_identifier(text: &str) -> u64 {
    let current_time = Utc::now().timestamp_nanos_opt(); // Получаем текущее время в наносекундах
    let mut hasher = DefaultHasher::new(); // Создаем хешер для вычисления хеша

    current_time.hash(&mut hasher);
    text.hash(&mut hasher);

    hasher.finish()
}


struct AppState {
    storage: Arc<Mutex<MemoryStorage>>,
}

impl AppState {
    pub async fn new() -> Self {
        let state = AppState {
            storage: Arc::new(Mutex::new(MemoryStorage::new())),
        };

        let _ = state.storage.lock().await.upsert_product(
            Product { 
                id: 123, 
                name: "член".to_string(), 
                description: "ЗАоуап".to_string(), 
                category_id: 623, 
                price: 1234, 
                quantity: 51, 
                active: true, 
                images: [].to_vec() 
            }).await;

        return state;
    }
}

fn process_error(error: StorageError) -> HttpResponse {
    match error {
        StorageError::InternalError(s) => 
            HttpResponse::InternalServerError().json(json!({"status": "error", "details": s})),
        StorageError::NotFoundError(s) => 
            HttpResponse::InternalServerError().json(json!({"status": "not found", "details": s})),
        StorageError::KeyCollisionError(s) => 
            HttpResponse::InternalServerError().json(json!({"status": "key collision", "details": s})),
    }
}


#[get("/products")]
async fn get_products(
    data: web::Data<AppState>,
    query: web::Query<ProductRequest>
) -> impl Responder {
    let storage = data.storage.lock().await;

    let query_info = query.into_inner();
    let products = storage.get_product(query_info).await;

    match products {
        Ok(p) => HttpResponse::Ok().json(p),
        Err(e) => process_error(e)
    }
}

#[post("/products")]
async fn create_product(
    data: web::Data<AppState>,
    product: web::Json<Product>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    match storage.upsert_product(product.into_inner()).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) => process_error(e)
        
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
            .service(create_product)
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}