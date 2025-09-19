#!/bin/bash

curl -s -X POST "http://localhost:8000/realms/test/protocol/openid-connect/token" \
  -u "application:application" \
  -H "Content-type: application/x-www-form-urlencoded" \
  -d "grant_type=password&scope=openid&username=admin&password=admin" | jq -r .access_token