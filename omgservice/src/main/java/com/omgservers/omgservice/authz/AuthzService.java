package com.omgservers.omgservice.authz;

import com.omgservers.omgservice.errors.PolicyNotFound;
import io.quarkus.keycloak.admin.client.common.runtime.KeycloakAdminClientConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.GroupsResource;
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
public class AuthzService {

    private static final Logger log = LoggerFactory.getLogger(AuthzService.class);
    final KeycloakAdminClientConfig config;
    final Keycloak keycloak;

    public AuthzService(final KeycloakAdminClientConfig config,
                        final Keycloak keycloak) {
        this.config = config;
        this.keycloak = keycloak;
    }

    public GroupRepresentation createGroup(final String name) {
        final var resource = getGroupsResource();

        final var groups = resource.groups(name, 0, 1);
        if (!groups.isEmpty()) {
            log.warn("Group {} already exists", name);
            return groups.getFirst();
        }

        final var representation = new GroupRepresentation();
        representation.setName(name);

        try (final var response = resource.add(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdGroup = resource.groups(name, 0, 1).getFirst();
                log.info("Created group {}", name);

                return createdGroup;
            } else {
                throw new InternalServerErrorException("Failed to create group " + name);
            }
        }
    }

    public ResourceRepresentation createResource(final String name,
                                                 final String type,
                                                 final String displayName,
                                                 final String uri,
                                                 final Set<String> scopeNames,
                                                 final Map<String, List<String>> attributes) {
        final var resource = getAuthorizationResource().resources();

        final var resources = resource.findByName(name);
        if (!resources.isEmpty()) {
            log.warn("Resource {} already exists", name);
            return resources.getFirst();
        }

        final var representation = new ResourceRepresentation();
        representation.setName(name);
        representation.setType(type);
        representation.setDisplayName(displayName);
        representation.setUris(Set.of(uri));
        final var scopeRepresentations = scopeNames.stream()
                .map(ScopeRepresentation::new)
                .collect(Collectors.toSet());
        representation.setScopes(scopeRepresentations);
        representation.setAttributes(attributes);

        try (final var response = resource.create(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdResource = resource.findByName(name).getFirst();
                log.info("Created resource {}", name);
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
            log.warn("Policy {} already exists", name);
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
                log.info("Created policy {}", name);
                return createdResource;
            } else {
                throw new InternalServerErrorException("Failed to create policy " + name);
            }
        }
    }

    public ScopePermissionRepresentation createPermission(final String name,
                                                          final ResourceRepresentation resource,
                                                          final String scope,
                                                          final Set<PolicyRepresentation> policies) {
        final var permissionsResource = getAuthorizationResource().permissions().scope();

        final var permission = permissionsResource.findByName(name);
        if (Objects.nonNull(permission)) {
            log.warn("Permission {} already exists", name);
            return permission;
        }

        final var representation = new ScopePermissionRepresentation();
        representation.setName(name);
        representation.addResource(resource.getId());
        representation.setScopes(Set.of(scope));
        final var policiesIds = policies.stream().map(PolicyRepresentation::getId).collect(Collectors.toSet());
        representation.setPolicies(policiesIds);
        representation.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);

        try (final var response = permissionsResource.create(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdResource = permissionsResource.findByName(name);
                log.info("Created permission {}", name);
                return createdResource;
            } else {
                throw new InternalServerErrorException("Failed to create permission");
            }
        }
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