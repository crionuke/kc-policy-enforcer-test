#!/bin/bash
set -e
set -o pipefail
source .env

NAME=$1

if [ -z "$NAME" ]; then
  echo "Usage: $0 <name>"
  exit 1
fi

echo "$(date) Executing $0 $@" >> .log

API_TOKEN=$(./echo_access_token.sh $API_USERNAME $API_PASSWORD)

echo "$(date) POST ${API_HOSTNAME}/v1/tenant" >> .log
curl -s -S --fail-with-body -X POST "${API_HOSTNAME}/v1/tenant" \
  -H "Authorization: Bearer ${API_TOKEN}" \
  -H "Content-type: application/json" \
  -d "{ \"name\": \"${NAME}\" }" | jq .
