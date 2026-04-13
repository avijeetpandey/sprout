# Sprout

Sprout is a Spring Boot based commerce backend focused on order flow, search sync, bulk product ingestion, and event-driven notifications.

## Tech Stack

- Java 17
- Spring Boot 4.0.5 (Web MVC, Validation, Security, Actuator)
- Spring Data JPA + PostgreSQL
- Spring Data Redis (cart cache/state)
- Spring Kafka (async workflows)
- Spring Data Elasticsearch (product search)
- Lombok + MapStruct
- Docker Compose for local infrastructure

## Features

- User APIs: create user, fetch user, block user, add addresses
- Product APIs: create product, paginated product listing
- Bulk product upload: async ingestion via Kafka topic (`product-bulk-ingestion`)
- Cart and checkout flow with Redis-backed cart storage
- Order tracking by order number
- Payment webhook simulation endpoint
- Async order notification flow via Kafka topic (`order-notification`)
- Product search backed by Elasticsearch with fuzzy search support

## Architecture at a Glance

- **PostgreSQL** stores primary transactional data (users, products, orders, payments)
- **Redis** stores cart snapshots for low-latency reads/writes
- **Kafka** decouples write path from side effects:
  - `product-search-sync`: product sync to Elasticsearch
  - `order-notification`: order confirmation notification
  - `product-bulk-ingestion`: async bulk product processing
  - `payment-success-topic` / `payment-failed-topic`: payment outcome events
- **Elasticsearch** powers search reads independent from transactional DB

## Prerequisites

- Java 17+
- Maven 3.9+ (or use `./mvnw`)
- Docker + Docker Compose

## Run Locally

### 1) Start infrastructure

```bash
docker compose up -d db redis kafka elasticsearch
```

Default exposed ports:

- PostgreSQL: `127.0.0.1:15432`
- Redis: `127.0.0.1:6379`
- Kafka: `127.0.0.1:9092`
- Elasticsearch: `127.0.0.1:9200`

### 2) Run the app on host

```bash
./mvnw spring-boot:run
```

App base URL: `http://localhost:9000`

### 3) Optional: run app container too

```bash
docker compose --profile full-stack up -d app
```

## Configuration Notes

- Main config: `src/main/resources/application.properties`
- API server port: `9000`
- Current config has `app.kafka.enabled=true`.
- Security config currently permits `"/api/v1/**"` without authentication.

## API Usage (cURL)

Set base URL once:

```bash
BASE_URL=http://localhost:9000
```

### 1) Create user

```bash
curl -X POST "$BASE_URL/api/v1/user" \
  -H "Content-Type: application/json" \
  -d '{
	"email": "john@example.com",
	"password": "Password@123"
  }'
```

### 2) Add address for user

```bash
curl -X POST "$BASE_URL/api/v1/user/address/1" \
  -H "Content-Type: application/json" \
  -d '{
	"street": "221B Baker Street",
	"city": "London",
	"zipCode": "NW16XE",
	"country": "UK",
	"addressType": "SHIPPING"
  }'
```

### 3) Add product

```bash
curl -X POST "$BASE_URL/api/v1/products/add" \
  -H "Content-Type: application/json" \
  -d '{
	"name": "iPhone 15",
	"description": "128GB smartphone",
	"price": 799.0,
	"stockQuantity": 20,
	"sku": "IP15-128-BLK",
	"productType": "ELECTRONICS"
  }'
```

### 4) List products (paginated)

```bash
curl "$BASE_URL/api/v1/products?page=0&size=10"
```

### 5) Add item to cart

```bash
curl -X POST "$BASE_URL/api/v1/orders/cart/add/1" \
  -H "Content-Type: application/json" \
  -d '{
	"productId": 1,
	"productName": "iPhone 15",
	"quantity": 1,
	"price": 799.00
  }'
```

### 6) Checkout order

```bash
curl -X POST "$BASE_URL/api/v1/orders/checkout" \
  -H "Content-Type: application/json" \
  -d '{
	"userId": 1,
	"email": "john@example.com",
	"shippingAddressId": 1,
	"paymentMethod": "CARD"
  }'
```

### 7) Track order

```bash
curl "$BASE_URL/api/v1/orders/track/<ORDER_NUMBER>"
```

### 8) Search products (Elasticsearch)

```bash
curl "$BASE_URL/api/v1/search?q=iphone"
```

### 9) Simulate payment webhook

```bash
curl -X POST "$BASE_URL/api/v1/payments/webhook/simulate" \
  -H "Content-Type: application/json" \
  -d '{
	"transactionId": "TXN-ABCDEFGH",
	"orderId": 1,
	"status": "SUCCESS",
	"providerMessage": "Captured"
  }'
```

### 10) Bulk upload products

Create sample payload file (`products.json` must be a JSON array):

```bash
cat > products.json << 'EOF'
[
  {
	"name": "Almond Milk",
	"description": "Unsweetened",
	"price": 3.99,
	"stockQuantity": 120,
	"sku": "ALM-001",
	"productType": "GROCERY"
  },
  {
	"name": "Orange Juice",
	"description": "1L bottle",
	"price": 2.49,
	"stockQuantity": 80,
	"sku": "OJ-001",
	"productType": "BEVERAGES"
  }
]
EOF
```

Upload:

```bash
curl -X POST "$BASE_URL/api/v1/products/bulk-upload" \
  -F "file=@products.json"
```

## Scalability Decisions Taken

- **Async event-driven side effects**: Kafka is used to decouple user-facing write APIs from downstream processing (notification, search sync, bulk processing).
- **Fast cart path with Redis**: cart operations avoid repeated relational joins and keep checkout reads quick.
- **Chunked bulk ingestion**: bulk products are processed in chunks (`CHUNK_SIZE = 500`) to keep memory bounded and reduce DB transaction pressure.
- **JPA batching enabled**: Hibernate batching (`hibernate.jdbc.batch_size=500`) reduces round-trips during heavy insert/update workloads.
- **Read model separation for search**: Elasticsearch index (`ProductIndex`) is maintained asynchronously from product writes, improving search latency and query flexibility.
- **Service decomposition readiness**: topic-based integration points (`order-notification`, `product-search-sync`, `payment-*`) make later service extraction easier.

## Observability

- Actuator is enabled and exposes endpoints under `/actuator`.

## Test

```bash
./mvnw test
```

