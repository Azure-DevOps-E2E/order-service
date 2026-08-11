# Order Service

Java 21 + Spring Boot service that validates users/products, calculates totals and stores orders in memory.

## API

- `GET /health`
- `GET /api/v1/orders`
- `GET /api/v1/orders/{id}`
- `POST /api/v1/orders`

## Configuration

| Variable | Default |
|---|---|
| `PORT` | `8083` |
| `USER_SERVICE_URL` | `http://localhost:8081` |
| `CATALOG_SERVICE_URL` | `http://localhost:8000` |

## Run and test

```bash
./mvnw test
./mvnw spring-boot:run
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

## Container

```bash
docker build -t order-service .
```
