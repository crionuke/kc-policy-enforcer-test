#!/bin/bash
set -e
set -o pipefail
source .env

TENANT_ID=$1
PROJECT_NAME=$2

if [ -z "$PROJECT_NAME" ]; then
  echo "Usage: $0 <tenant_id> <project_name>"
  exit 1
fi

echo "$(date) Executing $0 $@" >> .log

echo "$(date) POST ${API_HOSTNAME}/v1/tenants/${TENANT_ID}/projects" >> .log
curl -s -S --fail-with-body -X POST "${API_HOSTNAME}/v1/tenants/${TENANT_ID}/projects" \
  -H "Authorization: Bearer $(./echo_token.sh $API_USERNAME $API_PASSWORD)" \
  -H "Content-type: application/json" \
  -d "{ \"name\": \"${PROJECT_NAME}\", \"withRegistry\": true }" | jq .
