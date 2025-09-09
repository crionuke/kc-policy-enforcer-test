CREATE USER keycloak WITH PASSWORD 'keycloak';
CREATE SCHEMA IF NOT EXISTS keycloak AUTHORIZATION keycloak;
ALTER USER keycloak SET search_path TO keycloak;

CREATE USER tenants WITH PASSWORD 'tenants';
CREATE SCHEMA IF NOT EXISTS tenants AUTHORIZATION tenants;
ALTER USER tenants SET search_path TO tenants;