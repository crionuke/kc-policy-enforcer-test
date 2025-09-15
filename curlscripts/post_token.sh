#!/bin/bash
set -e
set -o pipefail
source .env

USERNAME=$1
PASSWORD=$2

if [ -z "$USERNAME" -o -z "$PASSWORD" ]; then
  echo "Usage: $0 <username> <password>"
  exit 1
fi

echo "$(date) Executing $0 $@" >> .log

curl -s -S --fail-with-body -X POST "${AUTH_HOSTNAME}/realms/omgservers/protocol/openid-connect/token" \
  -u "${AUTH_CLIENT_ID}:${AUTH_CLIENT_SECRET}" \
  -H "Content-type: application/x-www-form-urlencoded" \
  -d "grant_type=password&scope=openid&username=${USERNAME}&password=${PASSWORD}" | jq .