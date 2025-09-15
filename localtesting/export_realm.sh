#!/bin/bash
docker exec -it keycloak /opt/keycloak/bin/kc.sh export --file /tmp/omgservers-realm.json --users same_file --realm omgservers
docker exec -it keycloak cat /tmp/omgservers-realm.json > omgservers-realm.json
