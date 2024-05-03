mod model;
mod storage;

use model::category::*;
use model::product;
use model::order::*;
use model::product::*;
use storage::memory::*;
use storage::base::*;

use std::env;
use std::sync::Arc;
use actix_web::{get, post, put, delete, web, App, HttpResponse, HttpServer, Responder};
use serde_json::json;
use tokio::sync::Mutex;
use chrono::Utc;
use std::collections::hash_map::DefaultHasher;
use std::hash::{Hash, Hasher};


fn generate_identifier(text: &str) -> u64 {
    let current_time = Utc::now().timestamp_nanos_opt();
    let mut hasher = DefaultHasher::new();

    current_time.hash(&mut hasher);
    text.hash(&mut hasher);

    hasher.finish()
}

fn process_error(error: StorageError) -> HttpResponse {
    match error {
        StorageError::InternalError(s) => 
            HttpResponse::InternalServerError().json(json!({"status": "storage error", "details": s})),
        StorageError::NotFoundError(s) => 
            HttpResponse::InternalServerError().json(json!({"status": "storage entry not found", "details": s})),   
    }
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


// products

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
    body: web::Json<ProductRequest>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    let product_req = body.into_inner();

    if !product_req.name.is_some() {
        return HttpResponse::InternalServerError().json("missing name")
    }

    let possible_id = generate_identifier(product_req.name.clone().unwrap().as_ref());

    let possible_collision_res = storage.get_product(ProductRequest{ 
        id: Some(possible_id),
        name: None,
        description: None,
        category_id: None,
        price: None,
        quantity: None,
        active: None,
        images: None, }).await;

    if !possible_collision_res.is_ok() {
        HttpResponse::InternalServerError();
    }

    let possible_collision = possible_collision_res.unwrap();

    if !possible_collision.is_empty() {
        return HttpResponse::InternalServerError().json("collision")
    }

    let product = Product { 
        id: possible_id, 
        name: product_req.name.unwrap_or_default(), 
        description: product_req.description.unwrap_or_default(), 
        category_id: product_req.category_id.unwrap_or_default(), 
        price: product_req.price.unwrap_or_default(),
        quantity: product_req.quantity.unwrap_or_default(), 
        active: product_req.active.unwrap_or_default(), 
        images: product_req.images.unwrap_or_default(),
    };

    match storage.upsert_product(product).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) => process_error(e)
        
    }
}

#[put("/products/{id}")]
async fn update_product(
    data: web::Data<AppState>,
    product_id: web::Path<u64>,
    body: web::Json<ProductRequest>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    let product_req = body.into_inner();

    let possible_collision_res = storage.get_product(ProductRequest{ 
        id: Some(product_id.clone()),
        name: None,
        description: None,
        category_id: None,
        price: None,
        quantity: None,
        active: None,
        images: None,}).await;

    if !possible_collision_res.is_ok() {
        HttpResponse::InternalServerError();
    }

    let possible_collision = possible_collision_res.unwrap();

    if possible_collision.is_empty() {
        return HttpResponse::InternalServerError().json("not found")
    }

    let product = Product { 
        id: product_id.clone(), 
        name: product_req.name.unwrap_or_default(), 
        description: product_req.description.unwrap_or_default(), 
        category_id: product_req.category_id.unwrap_or_default(), 
        price: product_req.price.unwrap_or_default(),
        quantity: product_req.quantity.unwrap_or_default(), 
        active: product_req.active.unwrap_or_default(), 
        images: product_req.images.unwrap_or_default(),
    };

    match storage.upsert_product(product).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) => process_error(e)
        
    }
}

#[delete("/products/{id}")]
async fn delete_product(
    data: web::Data<AppState>,
    product_id: web::Path<u64>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    match storage.delete_product(product_id.into_inner()).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) => process_error(e)
        
    }
}


// orders

#[get("/orders")]
async fn get_orders(
    data: web::Data<AppState>,
    query: web::Query<OrderRequest>
) -> impl Responder {
    let storage = data.storage.lock().await;

    let query_info = query.into_inner();
    let categories = storage.get_order(query_info).await;

    match categories {
        Ok(p) => HttpResponse::Ok().json(p),
        Err(e) => process_error(e)
    }
}

#[post("/orders")]
async fn create_order(
    data: web::Data<AppState>,
    body: web::Json<OrderRequest>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    let order_req = body.into_inner();

    if !order_req.user_id.clone().is_some() {
        return HttpResponse::InternalServerError().json("missing user id")
    }


    let possible_id = generate_identifier(order_req.user_id.clone().unwrap().to_string().as_ref());

    let possible_collision_res = storage.get_order(
        OrderRequest{ 
            id: Some(possible_id), 
            user_id: None, 
            items: None, 
            status: None
        }).await;

    if !possible_collision_res.is_ok() {
        HttpResponse::InternalServerError();
    }

    if !possible_collision_res.unwrap().is_empty() {
        return HttpResponse::InternalServerError().json("collision")
    }


    let mut items: Vec<OrderItem> = Vec::new();

    for item_request in order_req.items.unwrap_or(vec![]) {
        if item_request.product_id.clone().is_none() || item_request.quantity.clone().is_none() {
            items.push(OrderItem{ 
                product_id: item_request.product_id.clone().unwrap(), 
                quantity: item_request.quantity.clone().unwrap() 
            });
        }
        else {
            return HttpResponse::InternalServerError().json("missing field for item")
        }
    }


    let order = Order{ 
        id: possible_id, 
        user_id: order_req.user_id.unwrap(), 
        items: items, 
        status: order_req.status.unwrap_or(Status::Failed) 
    };

    match storage.upsert_order(order).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) => process_error(e)
        
    }
}


// categories

#[get("/categories")]
async fn get_categories(
    data: web::Data<AppState>,
    query: web::Query<CategoryRequest>
) -> impl Responder {
    let storage = data.storage.lock().await;

    let query_info = query.into_inner();
    let categories = storage.get_category(query_info.id).await;

    match categories {
        Ok(p) => HttpResponse::Ok().json(p),
        Err(e) => process_error(e)
    }
}

// main

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let host = env::var("HOST").unwrap_or_else(|_| "127.0.0.1".to_string());
    let port = env::var("PORT").unwrap_or_else(|_| "8080".to_string());

    let app_state = web::Data::new(AppState::new().await);

    println!("Listening on {}:{}", host, port);

    HttpServer::new(move || {
        App::new()
            .app_data(app_state.clone())
            .service(get_products)
            .service(create_product)
            .service(update_product)
            .service(delete_product)
            .service(get_orders)
            .service(create_order)
            .service(get_categories)
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}