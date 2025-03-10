#!/bin/bash

set -e  # Exit immediately if any command fails

INDEX_NAME="correlation_results"
ELASTIC_URL="http://localhost:9200"
INDEX_URL="$ELASTIC_URL/$INDEX_NAME"

# Function to create index with dynamic mappings
create_index() {
  curl -X PUT "$INDEX_URL" -H "Content-Type: application/json" -d '{
      "settings": {
          "number_of_shards": 1,
          "index.mapping.total_fields.limit": 5000,
          "number_of_replicas": 1
      },
      "mappings": {
          "dynamic_templates": [
              {
                  "strings_as_keywords": {
                      "match_mapping_type": "string",
                      "mapping": {
                          "type": "keyword"
                      }
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
              "timestamp": { "type": "date" }
          }
      }
  }'
}

# Check if the index exists
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$INDEX_URL")

if [ "$HTTP_STATUS" -eq 404 ]; then
  echo "Index '$INDEX_NAME' does not exist. Creating index..."
  create_index
  echo "Index '$INDEX_NAME' created successfully."
else
  echo "Index '$INDEX_NAME' already exists or could not be checked."
fi
