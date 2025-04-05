#!/bin/bash

echo "🔄 Checking if Wazuh Indexer is running..."
until curl -s "http://wazuh-indexer:9200" >/dev/null; do
  echo "⏳ Waiting for Wazuh Indexer..."
  sleep 5
done

echo "✅ Wazuh Indexer is up!"

echo "🔄 Checking if Wazuh Manager is running..."
until curl -s "http://wazuh-manager:55000" >/dev/null; do
  echo "⏳ Waiting for Wazuh Manager..."
  sleep 5
done

echo "✅ Wazuh Manager is up!"
