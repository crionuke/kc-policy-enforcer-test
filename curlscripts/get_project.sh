#!/bin/bash
set -e
set -o pipefail
source .env

PROJECT_ID=$1

if [ -z "$PROJECT_ID" ]; then
  echo "Usage: $0 <project_id>"
  exit 1
fi

echo "$(date) Executing $0 $@" >> .log

echo "$(date) GET ${API_HOSTNAME}/v1/projects/${PROJECT_ID}" >> .log
curl -s -S --fail-with-body -X GET "${API_HOSTNAME}/v1/projects/${PROJECT_ID}" \
  -H "Authorization: Bearer $(./echo_token.sh $API_USERNAME $API_PASSWORD)" \
  -H "Content-type: application/json" | jq .
