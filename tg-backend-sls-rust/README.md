# Эндпоинты

## `/products`

### Получить товары `GET /products`

Возможные параметры строки - поля сущности `product`

### Создать новый товар `POST /products`

Тело содержит сущность `product` со всеми полями, кроме `id`

### Обновить товар `PUT /products/{id}`

Тело содержит сущность `product` со всеми полями, кроме `id`

### Удалить товар `DELETE /products/{id}`

## `/orders`

### Получить заказы `GET /orders`

Возможные параметры строки - поля сущности `order`

### Создать новый заказ `POST /order`

Тело содержит сущность `order`, кроме поля `id`

### Обновить заказ `PUT /order/{id}`

Тело содержит сущность `order`, кроме поля `id`

### Удалить заказ `DELETE /order/{id}`

## `/cart`

### Получить корзину `GET /cart/{id}`

### Обновить корзину `PUT /cart/{id}`

Тело содержит сущность `cart`, кроме поля `id`

## `/categories`

### Получить заказы `GET /categories`

Возможные параметры строки - `id`

### Создать новый заказ `POST /categories`

Тело содержит сущность `category`, кроме поля `id`

### Обновить заказ `PUT /categories/{id}`

Тело содержит сущность `category`, кроме поля `id`

### Удалить заказ `DELETE /categories/{id}`

# Сущности

## `Image`

```
Image {
    id: u64,
}
```

## `Product`

```
Product {
    id: u64,
    name: String,
    description: String,
    category_id: u64,
    price: u64,
    quantity: u64,
    active: bool,
    images: [Image],
}
```

## `Cart`

```
Cart {
    items: [OrderItem],
}
```

## `Order`

```
Order {
    id: u64,
    user_id: i64,
    items: [OrderItem],
    status: Status
}
```

## `OrderItem`

```
OrderItem {
    product_id: u64,
    quantity: u64,
}
```

## `OrderStatus`

```
OrderStatus {
    Failed,
    Pending,
    Shipping,
    Arrived,
    Finished
}
```

## `Category`

```
Category {
    id: u64,
    name: String
}
```

