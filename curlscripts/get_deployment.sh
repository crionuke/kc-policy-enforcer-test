#!/bin/bash
set -e
set -o pipefail
source .env

STAGE_ID=$1
DEPLOYMENT_ID=$2

if [ -z "${STAGE_ID}" -o -z "${DEPLOYMENT_ID}" ]; then
  echo "Usage: $0 <stage_id> <deployment_id>"
  exit 1
fi

echo "$(date) Executing $0 $@" >> .log

echo "$(date) GET ${API_HOSTNAME}/v1/stage/${STAGE_ID}/deployment/${DEPLOYMENT_ID}" >> .log
curl -s -S --fail-with-body -X GET "${API_HOSTNAME}/v1/stage/${STAGE_ID}/deployment/${DEPLOYMENT_ID}" \
  -H "Authorization: Bearer $(./echo_token.sh $API_USERNAME $API_PASSWORD)" \
  -H "Content-type: application/json" | jq .
