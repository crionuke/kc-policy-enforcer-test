#!/bin/bash
set -e
set -o pipefail
source .env

PROJECT_ID=$1
MAJOR_VERSION=$2
MINOR_VERSION=$3
PATCH_VERSION=$4

if [ -z "${PROJECT_ID}" -o -z "${MAJOR_VERSION}" -o -z "${MINOR_VERSION}" -o -z "${PATCH_VERSION}" ]; then
  echo "Usage: $0 <project_id> <major> <minor> <patch>"
  exit 1
fi

echo "$(date) Executing $0 $@" >> .log

echo "$(date) POST ${API_HOSTNAME}/v1/projects/${PROJECT_ID}/versions" >> .log
curl -s -S --fail-with-body -X POST "${API_HOSTNAME}/v1/projects/${PROJECT_ID}/versions" \
  -H "Authorization: Bearer $(./echo_token.sh $API_USERNAME $API_PASSWORD)" \
  -H "Content-type: application/json" \
  -d "{ \"major\": \"${MAJOR_VERSION}\", \"minor\": \"${MINOR_VERSION}\", \"patch\": \"${PATCH_VERSION}\" }" | jq .
