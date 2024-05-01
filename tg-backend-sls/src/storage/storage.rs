trait Storage {
    async fn get_user(id: Option<i64>, admin: Option<i64>) -> Result<User, Error>;
    async fn upsert_user(user: User) -> Result<(), Error>;
    async fn delete_user(id: i64) -> Result<(), Error>;

    async fn get_product(id: Option<i64>, category_id: Option<i64>, active: Option<bool>) -> Result<Product, Error>;
    async fn upsert_product(product: Product) -> Result<(), Error>;
    async fn delete_product(id: i64) -> Result<(), Error>;

    async fn get_order(id: Option<i64>, user_id: Option<i64>, status: Option<Status>) -> Result<Order, Status>;
    async fn upsert_order(order: Order) -> Result<(), Error>;
    async fn delete_order(id: i64) -> Result<(), Error>;

    async fn get_image(id: Option<i64>) -> Result<Image, Error>;
    async fn upsert_image(image: Image) -> Result<(), Error>;
    async fn delete_image(id: i64) -> Result<(), Error>;

    async fn get_category(id: Option<i64>) -> Result<Category, Error>;
    async fn upsert_category(category: Category) -> Result<(), Error>;
    async fn delete_category(id: i64) -> Result<(), Error>;
}

enum Error {
    NotFound(String),
    MultipleMatches(String),
    InternalError(String),
}