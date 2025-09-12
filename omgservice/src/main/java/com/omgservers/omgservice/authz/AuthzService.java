package com.omgservers.omgservice.authz;

import io.quarkus.keycloak.admin.client.common.runtime.KeycloakAdminClientConfig;
import jakarta.enterprise.context.ApplicationScoped;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.GroupsResource;

@ApplicationScoped
public class AuthzService {

    final KeycloakAdminClientConfig config;
    final Keycloak keycloak;

    public AuthzService(final KeycloakAdminClientConfig config,
                        final Keycloak keycloak) {
        this.config = config;
        this.keycloak = keycloak;
    }

    public GroupsResource getGroupsResource() {
        final var realm = config.realm();
        return keycloak.realm(realm).groups();
    }

    public AuthorizationResource getAuthorizationResource() {
        final var realm = config.realm();
        final var clients = keycloak.realm(realm).clients();
        final var clientId = config.clientId();
        final var representation = clients.findByClientId(clientId).getFirst();
        final var internalId = representation.getId();
        final var clientResource = clients.get(internalId);
        return clientResource.authorization();
    }
}