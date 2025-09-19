package com.omgservers.omgservice.authz;

import com.omgservers.omgservice.errors.PolicyNotFound;
import io.quarkus.keycloak.admin.client.common.runtime.KeycloakAdminClientConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.GroupPolicyRepresentation;
import org.keycloak.representations.idm.authorization.Logic;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class KeycloakService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakService.class);

    final KeycloakAdminClientConfig config;
    final Keycloak keycloak;

    public KeycloakService(final KeycloakAdminClientConfig config,
                           final Keycloak keycloak) {
        this.config = config;
        this.keycloak = keycloak;
    }

    public GroupRepresentation createGroup(final String name,
                                           final Map<String, List<String>> attributes) {
        final var resource = getGroupsResource();

        final var groups = resource.groups(name, 0, 1);
        if (!groups.isEmpty()) {
            LOGGER.warn("Group {} already exists", name);
            return groups.getFirst();
        }

        final var representation = new GroupRepresentation();
        representation.setName(name);
        representation.setAttributes(attributes);

        try (final var response = resource.add(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdGroup = resource.groups(name, 0, 1).getFirst();
                LOGGER.info("Created group {}", name);

                return createdGroup;
            } else {
                throw new InternalServerErrorException("Failed to create group " + name);
            }
        }
    }

    public void joinGroup(final String userId, final GroupRepresentation group) {
        final var resource = getUsersResource();

        final var userResource = resource.get(userId);
        userResource.joinGroup(group.getId());

        LOGGER.info("User {} joined group {}", userId, group.getName());
    }

    public ResourceRepresentation createResource(final String name,
                                                 final String type,
                                                 final String displayName,
                                                 final Set<String> uris,
                                                 final Set<String> scopes,
                                                 final Map<String, List<String>> attributes) {
        final var resource = getAuthorizationResource().resources();

        final var resources = resource.findByName(name);
        if (!resources.isEmpty()) {
            LOGGER.warn("Resource {} already exists", name);
            return resources.getFirst();
        }

        final var representation = new ResourceRepresentation();
        representation.setName(name);
        representation.setType(type);
        representation.setDisplayName(displayName);
        representation.setUris(uris);
        final var scopeRepresentations = scopes.stream()
                .map(ScopeRepresentation::new)
                .collect(Collectors.toSet());
        representation.setScopes(scopeRepresentations);
        representation.setAttributes(attributes);

        try (final var response = resource.create(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdResource = resource.findByName(name).getFirst();
                LOGGER.info("Created resource {}", name);
                return createdResource;
            } else {
                throw new InternalServerErrorException("Failed to create resource " + name);
            }
        }
    }

    public PolicyRepresentation findPolicyByNameRequired(final String name) {
        final var policiesResource = getAuthorizationResource().policies();
        final var policy = policiesResource.findByName(name);
        if (Objects.isNull(policy)) {
            throw new PolicyNotFound(name);
        }

        return policy;
    }

    public PolicyRepresentation createPolicy(final String name,
                                             final Set<GroupRepresentation> groups) {
        final var policiesResource = getAuthorizationResource().policies();

        final var policy = policiesResource.findByName(name);
        if (Objects.nonNull(policy)) {
            LOGGER.warn("Policy {} already exists", name);
            return policy;
        }

        final var representation = new GroupPolicyRepresentation();
        representation.setName(name);
        final var groupDefinitions = groups.stream().map(groupRepresentation ->
                        new GroupPolicyRepresentation.GroupDefinition(groupRepresentation.getId(), true))
                .collect(Collectors.toSet());
        representation.setGroups(groupDefinitions);
        representation.setLogic(Logic.POSITIVE);

        try (final var response = policiesResource.group().create(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdResource = policiesResource.findByName(name);
                LOGGER.info("Created policy {}", name);
                return createdResource;
            } else {
                throw new InternalServerErrorException("Failed to create policy " + name);
            }
        }
    }

    public ScopePermissionRepresentation createPermission(final String name,
                                                          final ResourceRepresentation resource,
                                                          final Set<String> scopes,
                                                          final Set<PolicyRepresentation> policies) {
        final var permissionsResource = getAuthorizationResource().permissions().scope();

        final var permission = permissionsResource.findByName(name);
        if (Objects.nonNull(permission)) {
            LOGGER.warn("Permission {} already exists", name);
            return permission;
        }

        final var representation = new ScopePermissionRepresentation();
        representation.setName(name);
        representation.addResource(resource.getId());
        representation.setScopes(scopes);
        final var policiesIds = policies.stream().map(PolicyRepresentation::getId).collect(Collectors.toSet());
        representation.setPolicies(policiesIds);
        representation.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);

        try (final var response = permissionsResource.create(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdResource = permissionsResource.findByName(name);
                LOGGER.info("Created permission {}", name);
                return createdResource;
            } else {
                throw new InternalServerErrorException("Failed to create permission");
            }
        }
    }

    UsersResource getUsersResource() {
        final var realm = config.realm();
        return keycloak.realm(realm).users();
    }

    GroupsResource getGroupsResource() {
        final var realm = config.realm();
        return keycloak.realm(realm).groups();
    }

    AuthorizationResource getAuthorizationResource() {
        final var realm = config.realm();
        final var clients = keycloak.realm(realm).clients();
        final var clientId = config.clientId();
        final var representation = clients.findByClientId(clientId).getFirst();
        final var internalId = representation.getId();
        final var clientResource = clients.get(internalId);
        return clientResource.authorization();
    }
}