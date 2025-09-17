#!/bin/bash
set -e
set -o pipefail
source .env

EVENT_ID=$1

if [ -z "$EVENT_ID" ]; then
  echo "Usage: $0 <event_id>"
  exit 1
fi

echo "$(date) Executing $0 $@" >> .log

echo "GET ${API_HOSTNAME}/v1/platform/events/${EVENT_ID}" >> .log
curl -s -S --fail-with-body -X GET "${API_HOSTNAME}/v1/platform/events/${EVENT_ID}/process" \
  -H "Authorization: Bearer $(./echo_token.sh $API_USERNAME $API_PASSWORD)" \
  -H "Content-type: application/json" | jq .
