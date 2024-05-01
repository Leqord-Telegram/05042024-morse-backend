mod model;

mod storage;
use storage::memory::*;

use std::env;
use actix_web::{get, post, App, HttpResponse, HttpServer, Responder};


#[get("/categories")]
async fn categories() -> impl Responder {
    HttpResponse::Ok().body("Hello world!")
}

#[post("/echo")]
async fn echo(req_body: String) -> impl Responder {
    HttpResponse::Ok().body(req_body)
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let host = env::var("HOST").unwrap_or_else(|_| "127.0.0.1".to_string());
    let port = env::var("PORT").unwrap_or_else(|_| "8080".to_string());

    let mut storage = MemoryStorage::new();

    println!("Listening on {}:{}", host, port);

    HttpServer::new(|| {
        App::new()
            .service(echo)
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}