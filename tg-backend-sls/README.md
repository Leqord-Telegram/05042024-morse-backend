# API

## Сущности

### User

```Json
{
    id: int,
    name: string,
    admin: bool,
}
```

### Product

```json
{
    id: int,
    name: string,
    destription: string,
    price: int,
    quantity: int,
    active: bool,
    images: [Image]
}
```

### Order

```Json
{
    id: int,
    user_id: int,
    items: [Item],
    status: Failed | Pending | Shipping | Arrived| Finished
}
```

### Order Item

```Json
{
    id: int,
    product_id: int,
    quantity: uint,
}
```

### Image 

```Json
{
    id: int,
}
```

### Category

```Json
{
    id: int,
    name: string
}
```

