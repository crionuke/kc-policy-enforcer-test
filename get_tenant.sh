#!/bin/bash

curl -s -S --fail-with-body http://localhost:8080/v1/tenants/1 \
  -H "Authorization: Bearer $(./get_token.sh)"