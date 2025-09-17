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

./get_token.sh $USERNAME $PASSWORD | jq -r .access_token