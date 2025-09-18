#!/bin/bash
set -e
set -o pipefail
source .env

TENANT_ID=$1
STAGE_NAME=$2

if [ -z "$STAGE_NAME" ]; then
  echo "Usage: $0 <tenant_id> <stage_name>"
  exit 1
fi

echo "$(date) Executing $0 $@" >> .log

echo "$(date) POST ${API_HOSTNAME}/v1/tenants/${TENANT_ID}/stages" >> .log
curl -s -S --fail-with-body -X POST "${API_HOSTNAME}/v1/tenants/${TENANT_ID}/stages" \
  -H "Authorization: Bearer $(./echo_token.sh $API_USERNAME $API_PASSWORD)" \
  -H "Content-type: application/json" \
  -d "{ \"name\": \"${STAGE_NAME}\" }" | jq .
