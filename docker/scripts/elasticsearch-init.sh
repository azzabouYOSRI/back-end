#!/bin/bash

set -e  # Exit immediately if any command fails

ELASTIC_URL="http://localhost:9201"  # Updated port to match standalone Elasticsearch
INDEX_NAME="correlation_results"
INDEX_URL="$ELASTIC_URL/$INDEX_NAME"

# ✅ Step 1: Check if Elasticsearch is up
echo "🔄 Checking if Elasticsearch is running..."
until curl -s "$ELASTIC_URL" >/dev/null; do
  echo "⏳ Waiting for Elasticsearch..."
  sleep 5
done
echo "✅ Elasticsearch is up!"

# ✅ Step 2: Check if index exists
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$INDEX_URL")

if [ "$HTTP_STATUS" -eq 404 ]; then
  echo "🆕 Index '$INDEX_NAME' not found. Creating it now..."

  curl -X PUT "$INDEX_URL" -H "Content-Type: application/json" -u admin:${ELASTIC_PASSWORD:-changeme} -d '{
      "settings": {
          "number_of_shards": 1,
          "number_of_replicas": 1,
          "index.mapping.total_fields.limit": 10000  # Allows high field count
      },
      "mappings": {
          "dynamic": true,  # ✅ Enable auto-mapping of new fields
          "dynamic_templates": [
              {
                  "strings_as_keywords": {
                      "match_mapping_type": "string",
                      "mapping": { "type": "keyword" }
                  }
              },
              {
                  "long_numbers": {
                      "match_mapping_type": "long",
                      "mapping": { "type": "long" }
                  }
              },
              {
                  "booleans": {
                      "match_mapping_type": "boolean",
                      "mapping": { "type": "boolean" }
                  }
              },
              {
                  "dates": {
                      "match_mapping_type": "date",
                      "mapping": { "type": "date" }
                  }
              }
          ],
          "properties": {
              "alertId": { "type": "keyword" },
              "isAlert": { "type": "boolean" },
              "alertReason": { "type": "text" },
              "eventType": { "type": "keyword" },
              "entityId": { "type": "keyword" },
              "entityType": { "type": "keyword" },
              "message": { "type": "text" },
              "timestamp": { "type": "date" },
              "extraFields": { "type": "object", "enabled": true }  # ✅ Accepts any new fields dynamically
          }
      }
  }'

  echo "✅ Index '$INDEX_NAME' created successfully!"
else
  echo "⚠️ Index '$INDEX_NAME' already exists. Updating mappings..."

  curl -X PUT "$INDEX_URL/_mapping" -H "Content-Type: application/json" -u admin:${ELASTIC_PASSWORD:-changeme} -d '{
      "dynamic": true,
      "properties": {
          "extraFields": { "type": "object", "enabled": true }
      }
  }'

  echo "✅ Index '$INDEX_NAME' mappings updated dynamically."
fi

echo "🚀 Elasticsearch setup complete!"
