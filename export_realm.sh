#!/bin/bash
docker exec -it keycloak /opt/keycloak/bin/kc.sh export --file /tmp/test-realm.json --users same_file --realm test
docker exec -it keycloak cat /tmp/test-realm.json > test-realm.json