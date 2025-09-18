#!/bin/bash
set -e
set -o pipefail
source .env

PROJECT_ID=$1
VERSION_ID=$2

if [ -z "${PROJECT_ID}" -o -z "${VERSION_ID}" ]; then
  echo "Usage: $0 <project_id> <version_id>"
  exit 1
fi

echo "$(date) Executing $0 $@" >> .log

echo "$(date) GET ${API_HOSTNAME}/v1/project/${PROJECT_ID}/version/${VERSION_ID}" >> .log
curl -s -S --fail-with-body -X GET "${API_HOSTNAME}/v1/project/${PROJECT_ID}/version/${VERSION_ID}" \
  -H "Authorization: Bearer $(./echo_token.sh $API_USERNAME $API_PASSWORD)" \
  -H "Content-type: application/json" | jq .
