# kc-policy-enforcer-test

# How to start

- ./start_keycloak.sh
- ./mvnw quarkus:dev

# How to test

- The first request ./get_tenant.sh returns 403 Forbidden
- After a 1-second delay, the next request ./get_tenant.sh returns 200 OK 
