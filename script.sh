#!/bin/bash

BASE_URL="http://localhost:9000/api/v1"
NUM_USERS=1000
KAFKA_CONTAINER="sprout-kafka"
TOPIC="order-notification"

echo "🚀 Starting Real-World Simulation for $NUM_USERS users..."

# 1. Setup Product
echo "📦 Initializing Catalog Item..."
PRODUCT_ID=$(curl -s -X POST "$BASE_URL/products/add" \
  -H "Content-Type: application/json" \
  -d '{"name": "Sony XM5", "description": "Stress Unit", "price": 299.99, "stockQuantity": 1000, "sku": "REAL-WORLD-STRESS", "productType": "ELECTRONICS"}' \
  | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

# Fallback if grep fails
if [ -z "$PRODUCT_ID" ]; then PRODUCT_ID=1; fi
echo "✅ Product ready (ID: $PRODUCT_ID)"

sleep 2

user_journey() {
    local ID=$1
    local EMAIL="pro_user_${ID}@sprout.com"

    # Register User
    curl -s -o /dev/null -X POST "$BASE_URL/user" \
      -H "Content-Type: application/json" \
      -d "{\"email\": \"$EMAIL\", \"password\": \"Pass123!\"}"

    # Stagger for DB commit
    sleep 0.2

    # Add Address
    curl -s -o /dev/null -X POST "$BASE_URL/user/address/$ID" \
      -H "Content-Type: application/json" \
      -d "{\"street\": \"$ID AI Way\", \"city\": \"BLR\", \"zipCode\": \"560\", \"country\": \"IN\", \"addressType\": \"SHIPPING\"}"

    # Add to Cart
    curl -s -o /dev/null -X POST "$BASE_URL/orders/cart/add/$ID" \
      -H "Content-Type: application/json" \
      -d "{\"productId\": $PRODUCT_ID, \"productName\": \"Sony XM5\", \"quantity\": 1, \"price\": 299.99}"

    # Final Checkout
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/orders/checkout" \
      -H "Content-Type: application/json" \
      -d "{\"userId\": $ID, \"email\": \"$EMAIL\", \"shippingAddressId\": $ID}")

    if [ "$STATUS" -eq 200 ] || [ "$STATUS" -eq 201 ]; then
        echo "✅ User $ID: Order Placed"
    else
        echo "❌ User $ID: Failed (HTTP $STATUS)"
    fi
}

export -f user_journey
echo "🔥 Spawning $NUM_USERS concurrent journeys..."
for i in $(seq 1 $NUM_USERS); do
    user_journey $i &
    if (( $i % 15 == 0 )); then sleep 0.5; fi
done

wait
echo -e "\n🏁 Simulation HTTP wave finished. Waiting for Kafka..."
sleep 5

# --- FIXED KAFKA VERIFICATION ---
echo "📊 Verifying Kafka Notifications..."

# We use kafka-run-class correctly here by passing the container name first
MESSAGES=$(docker exec $KAFKA_CONTAINER kafka-run-class kafka.tools.ConsoleConsumer \
  --bootstrap-server localhost:9092 \
  --topic $TOPIC \
  --from-beginning \
  --max-messages $NUM_USERS \
  --timeout-ms 5000 2>/dev/null | grep -c "orderNumber")

if [ "$MESSAGES" -gt 0 ]; then
    echo "🏆 SUCCESS! Found $MESSAGES notifications in Kafka."
    echo "Check your IntelliJ console to see the Consumer processing them."
else
    echo "❌ Kafka check failed to find messages. Check 'docker logs sprout-kafka' for issues."
fi