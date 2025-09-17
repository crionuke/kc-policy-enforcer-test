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

echo "$(date) POST ${API_HOSTNAME}/v1/my/tenants" >> .log
curl -s -S --fail-with-body -X POST "${API_HOSTNAME}/v1/my/tenants" \
  -H "Authorization: Bearer $(./echo_token.sh $API_USERNAME $API_PASSWORD)" \
  -H "Content-type: application/json" \
  -d "{ \"name\": \"${NAME}\" }" | jq .

