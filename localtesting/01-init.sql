CREATE USER keycloak WITH PASSWORD 'keycloak';
CREATE SCHEMA IF NOT EXISTS keycloak AUTHORIZATION keycloak;
ALTER USER keycloak SET search_path TO keycloak;

CREATE USER omgservice WITH PASSWORD 'omgservice';
CREATE SCHEMA IF NOT EXISTS omgservice AUTHORIZATION omgservice;
ALTER USER omgservice SET search_path TO omgservice;