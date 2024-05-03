# API

## Сущности


### Product

```
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

```
{
    id: int,
    user_id: int,
    items: [Item],
    status: Failed | Pending | Shipping | Arrived| Finished
}
```

### Order Item

```
{
    id: int,
    product_id: int,
    quantity: uint,
}
```

### Image 

```
{
    id: int,
}
```

### Category

```
{
    id: int,
    name: string
}
```

