#!/bin/bash

# Configuration
URL="http://localhost:9000/api/v1/products/bulk-upload"
DATA_FILE="bulk_products_1k.json"
NUM_PRODUCTS=2000
TYPES=("ELECTRONICS" "FOOD" "CLOTHES" "BEVERAGES" "APPLIANCE")

echo "---------------------------------------------------"
echo "🚀 SPROUT HIGH-SCALE INGESTION ENGINE"
echo "---------------------------------------------------"

# 1. Generate JSON Data
echo "📝 Step 1: Generating $NUM_PRODUCTS product records..."
echo "[" > $DATA_FILE

for i in $(seq 1 $NUM_PRODUCTS); do
    TYPE=${TYPES[$((RANDOM % 5))]}
    # Generate a random price between 10.00 and 1000.00
    PRICE=$(awk -v min=10 -v max=1000 'BEGIN{srand(); printf "%.2f", min+rand()*(max-min)}')
    
    # Write JSON object to file
    cat <<EOF >> $DATA_FILE
  {
    "name": "Product $i",
    "description": "Bulk ingestion record for item $i in the 10k stress test suite.",
    "price": $PRICE,
    "stockQuantity": $((RANDOM % 500 + 1)),
    "sku": "SKU-$i-$(date +%s%N)",
    "productType": "$TYPE"
  }$([[ $i -eq $NUM_PRODUCTS ]] && echo "" || echo ",")
EOF

    # Progress Indicator
    if (( $i % 500 == 0 )); then
        echo "   > Generated $i / $NUM_PRODUCTS..."
    fi
done

echo "]" >> $DATA_FILE
echo "✅ $DATA_FILE generation complete."

# 2. Upload to API
echo -e "\n📤 Step 2: Uploading to $URL..."
echo "---------------------------------------------------"

# Use time to measure how long the network request takes
# The actual processing happens async via Kafka
START_TIME=$(date +%s)

RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "$URL" \
  -F "file=@$DATA_FILE" \
  -H "Accept: application/json")

END_TIME=$(date +%s)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d':' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')

# 3. Final Summary
echo "---------------------------------------------------"
if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 202 ]; then
    echo "🏆 SUCCESS: API accepted the batch in $((END_TIME - START_TIME))s."
    echo "📄 Server Response: $BODY"
else
    echo "❌ FAILED: Server returned HTTP $HTTP_CODE"
    echo "📄 Error Body: $BODY"
fi

# 4. Cleanup (Optional)
# rm $DATA_FILE
echo "---------------------------------------------------"
echo "🏁 Execution Finished."
echo "💡 Monitoring Tip: Watch IntelliJ logs for '💾 Saved chunk of 500 products'"
