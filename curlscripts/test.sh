#!/bin/bash

TENANT_ID=$(./create_tenant.sh $(uuidgen) | jq .id)
echo Created Tenant $TENANT_ID

sleep 1

./get_authorization.sh "resource:omg:tenant:${TENANT_ID}#scope:omg:tenant:view"
./get_tenant.sh ${TENANT_ID}