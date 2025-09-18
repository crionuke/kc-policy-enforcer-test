#!/bin/bash
set -e
set -o pipefail
source .env

STAGE_ID=$1
VERSION_ID=$2

if [ -z "${STAGE_ID}" -o -z "${VERSION_ID}" ]; then
  echo "Usage: $0 <stage_id> <version_id>"
  exit 1
fi

echo "$(date) Executing $0 $@" >> .log

echo "$(date) POST ${API_HOSTNAME}/v1/stages/${STAGE_ID}/deployments" >> .log
curl -s -S --fail-with-body -X POST "${API_HOSTNAME}/v1/stages/${STAGE_ID}/deployments" \
  -H "Authorization: Bearer $(./echo_token.sh $API_USERNAME $API_PASSWORD)" \
  -H "Content-type: application/json" \
  -d "{ \"versionId\": \"${VERSION_ID}\" }" | jq .
