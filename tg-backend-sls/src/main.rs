mod model;
use model::order;

mod storage;
use storage::memory::*;

use std::env;
use actix_web::{get, post, web, App, HttpResponse, HttpServer, Responder};


#[get("/json1")]
async fn json1() -> impl Responder {
    let item = order::Item {
        id: 123,
        product_id: 512,
        quantity: 124513,
    };

    let order = order::Order{
        id: 12132,
        user_id: 7345,
        items: vec! [item,],
        status: order::Status::Failed
    };

    HttpResponse::Ok().json(order)
}

#[get("/")]
async fn hello() -> impl Responder {
    HttpResponse::Ok().body("Hello world!")
}

#[post("/echo")]
async fn echo(req_body: String) -> impl Responder {
    HttpResponse::Ok().body(req_body)
}

async fn manual_hello() -> impl Responder {
    HttpResponse::Ok().body("Hey there!")
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let host = env::var("HOST").unwrap_or_else(|_| "127.0.0.1".to_string());
    let port = env::var("PORT").unwrap_or_else(|_| "8080".to_string());
    let mut storage = MemoryStorage::new();

    println!("Listening on {}:{}", host, port);

    HttpServer::new(|| {
        App::new()
            .service(hello)
            .service(echo)
            .service(json1)
            .route("/hey", web::get().to(manual_hello))
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}