#!/bin/bash
set -e
set -o pipefail
source .env

echo "$(date) Executing $0 $@" >> .log

curl -s -S --fail-with-body -X GET "${API_HOSTNAME}/v1/platform/events" \
  -H "Authorization: Bearer $(./echo_token.sh $API_USERNAME $API_PASSWORD)" \
  -H "Content-type: application/json" | jq .
