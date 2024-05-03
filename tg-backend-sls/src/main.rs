mod model;
mod storage;

use model::category::*;
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

    let found_product = storage.get_product(ProductRequest{ 
        id: Some(product_id.clone()),
        name: None,
        description: None,
        category_id: None,
        price: None,
        quantity: None,
        active: None,
        images: None,}).await;

    if !found_product.is_ok() {
        HttpResponse::InternalServerError();
    }

    let possible_collision = found_product.unwrap();

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
        active: product_req.active.unwrap_or(false), 
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

    let item_reqs = order_req.items.unwrap_or(vec![]);
    let mut items: Vec<OrderItem> = Vec::new();

    for item_request in item_reqs {
        if item_request.product_id.is_none() {
            return HttpResponse::InternalServerError().json("missing product id for item")
        }

        if item_request.quantity.is_none() {
            return HttpResponse::InternalServerError().json("missing quantity for item")
        }

        items.push(OrderItem{ 
            product_id: item_request.product_id.clone().unwrap(), 
            quantity: item_request.quantity.clone().unwrap() 
        });
    }


    let order = Order{ 
        id: possible_id, 
        user_id: order_req.user_id.unwrap(), 
        items: items, 
        status: order_req.status.unwrap_or(OrderStatus::Failed) 
    };

    match storage.upsert_order(order).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) => process_error(e)
        
    }
}

#[put("/orders/{id}")]
async fn update_order(
    data: web::Data<AppState>,
    order_id: web::Path<u64>,
    body: web::Json<OrderRequest>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    let order_req = body.into_inner();

    if !order_req.user_id.clone().is_some() {
        return HttpResponse::InternalServerError().json("missing user id")
    }

    let found_order = storage.get_order(OrderRequest{ 
        id: Some(order_id.clone()), 
        user_id: None, 
        items: None, 
        status: None 
    }).await;

    if !found_order.is_ok() {
        HttpResponse::InternalServerError();
    }

    if found_order.unwrap().is_empty() {
        return HttpResponse::InternalServerError().json("not found")
    }

    let mut items: Vec<OrderItem> = Vec::new();

    for item_request in order_req.items.unwrap_or(vec![]) {
        if item_request.product_id.is_none() {
            return HttpResponse::InternalServerError().json("missing product id for item")
        }

        if item_request.quantity.is_none() {
            return HttpResponse::InternalServerError().json("missing quantity for item")
        }

        items.push(OrderItem{ 
            product_id: item_request.product_id.clone().unwrap(), 
            quantity: item_request.quantity.clone().unwrap() 
        });
    }

    let order = Order{ 
        id: order_id.clone(), 
        user_id: order_req.user_id.unwrap(), 
        items: items, 
        status: order_req.status.unwrap_or(OrderStatus::Failed) 
    };

    match storage.upsert_order(order).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) => process_error(e)
        
    }
}

#[delete("/orders/{id}")]
async fn delete_order(
    data: web::Data<AppState>,
    order_id: web::Path<u64>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    match storage.delete_order(order_id.into_inner()).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) => process_error(e)
        
    }
}


// cart

#[get("/cart/{id}")]
async fn get_cart(
    data: web::Data<AppState>,
    user_id: web::Path<i64>
) -> impl Responder {
    let storage = data.storage.lock().await;

    let user_id = user_id.into_inner();
    let cart = storage.get_cart(Some(user_id)).await;

    match cart {
        Ok(p) => HttpResponse::Ok().json(p),
        Err(e) => process_error(e)
    }
}

#[put("/cart/{id}")]
async fn update_cart(
    data: web::Data<AppState>,
    user_id: web::Path<i64>,
    body: web::Json<Cart>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    match storage.upsert_cart(user_id.into_inner(), body.into_inner()).await {
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

#[post("/categories")]
async fn create_category(
    data: web::Data<AppState>,
    body: web::Json<CategoryRequest>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    let category_req = body.into_inner();

    if !category_req.name.clone().is_some() {
        return HttpResponse::InternalServerError().json("missing name")
    }

    let possible_id = generate_identifier(category_req.name.clone().unwrap().to_string().as_ref());

    let possible_collision_res = storage.get_category(Some(possible_id)).await;

    if !possible_collision_res.is_ok() {
        HttpResponse::InternalServerError();
    }

    if !possible_collision_res.unwrap().is_empty() {
        return HttpResponse::InternalServerError().json("collision")
    }

    let category = Category{ 
        id: possible_id, 
        name: category_req.name.unwrap()
    };

    match storage.upsert_category(category).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) => process_error(e)
        
    }
}

#[put("/categories/{id}")]
async fn update_category (
    data: web::Data<AppState>,
    category_id: web::Path<u64>,
    body: web::Json<CategoryRequest>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    let category_req = body.into_inner();

    if !category_req.name.clone().is_some() {
        return HttpResponse::InternalServerError().json("missing user id")
    }

    let found_category = storage.get_category(Some(category_id.clone())).await;

    if !found_category.is_ok() {
        HttpResponse::InternalServerError();
    }

    if found_category.unwrap().is_empty() {
        return HttpResponse::InternalServerError().json("not found")
    }

    let category = Category{ 
        id: category_id.into_inner(), 
        name: category_req.name.unwrap()
    };

    match storage.upsert_category(category).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
        Err(e) => process_error(e)
        
    }
}

#[delete("/categories/{id}")]
async fn delete_category (
    data: web::Data<AppState>,
    order_id: web::Path<u64>
) -> HttpResponse {
    let mut storage = data.storage.lock().await;

    match storage.delete_order(order_id.into_inner()).await {
        Ok(_) => HttpResponse::Created().json(json!({"status": "success"})),
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
            .service(update_order)
            .service(delete_order)
            .service(get_categories)
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}