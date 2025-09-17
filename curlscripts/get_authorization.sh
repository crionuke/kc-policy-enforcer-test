#!/bin/bash
set -e
set -o pipefail
source .env

RESOURCE_SCOPE=$1

echo "$(date) Executing $0 $@" >> .log

SUBJECT_TOKEN=$(./echo_token.sh $API_USERNAME $API_PASSWORD)

if [ -z ${RESOURCE_SCOPE} ]; then
  curl -s -S --fail-with-body -X POST "${AUTH_HOSTNAME}/realms/omgservers/protocol/openid-connect/token" \
    -u ${AUTH_CLIENT_ID}:${AUTH_CLIENT_SECRET} \
    -H "Content-type: application/x-www-form-urlencoded" \
    -d "grant_type=urn:ietf:params:oauth:grant-type:uma-ticket" \
    -d "audience=${AUTH_CLIENT_ID}" \
    -d "subject_token=$SUBJECT_TOKEN" | jq .
else
  curl -s -S --fail-with-body -X POST "${AUTH_HOSTNAME}/realms/omgservers/protocol/openid-connect/token" \
    -u ${AUTH_CLIENT_ID}:${AUTH_CLIENT_SECRET} \
    -H "Content-type: application/x-www-form-urlencoded" \
    -d "grant_type=urn:ietf:params:oauth:grant-type:uma-ticket" \
    -d "audience=${AUTH_CLIENT_ID}" \
    -d "subject_token=$SUBJECT_TOKEN" | jq .
fi