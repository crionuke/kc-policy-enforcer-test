package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.authz.AuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
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
public class TenantAuthzService {
    static final Logger log = LoggerFactory.getLogger(TenantAuthzService.class);

    static private final String VIEW_SCOPE = "scope:omg:tenant:view";
    static private final String MANAGE_SCOPE = "scope:omg:tenant:manage";
    static private final String ADMIN_SCOPE = "scope:omg:tenant:admin";

    static private final String RESOURCE_NAME = "resource:omg:tenant:%d";
    static private final String RESOURCE_DISPLAY_NAME = "Tenant %d";
    static private final String RESOURCE_TYPE = "type:omg:tenant";
    static private final String RESOURCE_URI = "/tenant/%d";

    static private final String VIEWERS_GROUP_NAME = "group:omg:tenant:%d:viewers";
    static private final String MANAGERS_GROUP_NAME = "group:omg:tenant:%d:managers";
    static private final String ADMINS_GROUP_NAME = "group:omg:tenant:%d:admins";

    static private final String VIEWERS_POLICY_NAME = "policy:omg:tenant:%d:viewers";
    static private final String MANAGERS_POLICY_NAME = "policy:omg:tenant:%d:managers";
    static private final String ADMINS_POLICY_NAME = "policy:omg:tenant:%d:admins";

    static private final String VIEW_PERMISSION_NAME = "permission:omg:tenant:%d:view";
    static private final String MANAGE_PERMISSION_NAME = "permission:omg:tenant:%d:manage";
    static private final String ADMIN_PERMISSION_NAME = "permission:omg:tenant:%d:admin";

    static private final String TENANT_ATTRIBUTE_TENANT_ID = "tenant_id";

    final AuthzService authzService;

    public TenantAuthzService(final AuthzService authzService) {
        this.authzService = authzService;
    }

    public ResourceRepresentation createResourceIfAny(final Long tenantId) {
        final var resource = authzService.getAuthorizationResource().resources();

        final var name = RESOURCE_NAME.formatted(tenantId);

        final var resources = resource.findByName(name);
        if (!resources.isEmpty()) {
            log.warn("Resource {} for tenant {} already exists", name, tenantId);
            return resources.getFirst();
        }

        final var representation = new ResourceRepresentation();
        representation.setName(name);
        representation.setType(RESOURCE_TYPE);
        representation.setDisplayName(RESOURCE_DISPLAY_NAME.formatted(tenantId));
        representation.setUris(Set.of(RESOURCE_URI.formatted(tenantId)));
        representation.addScope(new ScopeRepresentation(VIEW_SCOPE));
        representation.addScope(new ScopeRepresentation(MANAGE_SCOPE));
        representation.addScope(new ScopeRepresentation(ADMIN_SCOPE));
        representation.setAttributes(Map.of(TENANT_ATTRIBUTE_TENANT_ID, List.of(tenantId.toString())));

        try (final var response = resource.create(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdResource = resource.findByName(name).getFirst();
                log.info("Created resource {} for tenant {}", name, tenantId);
                return createdResource;
            } else {
                throw new InternalServerErrorException("Failed to create tenant resource");
            }
        }
    }

    public GroupRepresentation createViewersGroupIfAny(final Long tenantId) {
        final var name = VIEWERS_GROUP_NAME.formatted(tenantId);
        return createGroupIfAny(tenantId, name);
    }

    public GroupRepresentation createManagersGroupIfAny(final Long tenantId) {
        final var name = MANAGERS_GROUP_NAME.formatted(tenantId);
        return createGroupIfAny(tenantId, name);
    }

    public GroupRepresentation createAdminsGroupIfAny(final Long tenantId) {
        final var name = ADMINS_GROUP_NAME.formatted(tenantId);
        return createGroupIfAny(tenantId, name);
    }

    public PolicyRepresentation createViewersPolicyIfAny(final Long tenantId,
                                                         final GroupRepresentation viewersGroup) {
        final var name = VIEWERS_POLICY_NAME.formatted(tenantId);
        return createPolicyIfAny(tenantId, name, Set.of(viewersGroup));
    }

    public PolicyRepresentation createManagersPolicyIfAny(final Long tenantId,
                                                          final GroupRepresentation managersGroup) {
        final var name = MANAGERS_POLICY_NAME.formatted(tenantId);
        return createPolicyIfAny(tenantId, name, Set.of(managersGroup));
    }

    public PolicyRepresentation createAdminsPolicyIfAny(final Long tenantId,
                                                        final GroupRepresentation adminsGroup) {
        final var name = ADMINS_POLICY_NAME.formatted(tenantId);
        return createPolicyIfAny(tenantId, name, Set.of(adminsGroup));
    }

    public ScopePermissionRepresentation createViewPermissionIfAny(final Long tenantId,
                                                                   final ResourceRepresentation resource,
                                                                   final Set<PolicyRepresentation> policies) {
        final var name = VIEW_PERMISSION_NAME.formatted(tenantId);
        return createPermissionIfAny(tenantId, name, resource, VIEW_SCOPE, policies);
    }

    public ScopePermissionRepresentation createManagePermissionIfAny(final Long tenantId,
                                                                     final ResourceRepresentation resource,
                                                                     final Set<PolicyRepresentation> policies) {
        final var name = MANAGE_PERMISSION_NAME.formatted(tenantId);
        return createPermissionIfAny(tenantId, name, resource, MANAGE_SCOPE, policies);
    }

    public ScopePermissionRepresentation createAdminPermissionIfAny(final Long tenantId,
                                                                    final ResourceRepresentation resource,
                                                                    final Set<PolicyRepresentation> policies) {
        final var name = ADMIN_PERMISSION_NAME.formatted(tenantId);
        return createPermissionIfAny(tenantId, name, resource, ADMIN_SCOPE, policies);
    }

    private GroupRepresentation createGroupIfAny(final Long tenantId, final String name) {
        final var resource = authzService.getGroupsResource();

        final var groups = resource.groups(VIEWERS_GROUP_NAME, 0, 1);
        if (!groups.isEmpty()) {
            log.warn("Group {} for tenant {} already exists", name, tenantId);
            return groups.getFirst();
        }

        final var representation = new GroupRepresentation();
        representation.setName(name);

        try (final var response = resource.add(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdGroup = resource.groups(name, 0, 1).getFirst();
                log.info("Created group {} for tenant {}", name, tenantId);

                return createdGroup;
            } else {
                throw new InternalServerErrorException("Failed to create tenant group");
            }
        }
    }

    private PolicyRepresentation createPolicyIfAny(final Long tenantId,
                                                   final String name,
                                                   final Set<GroupRepresentation> groups) {
        final var policiesResource = authzService.getAuthorizationResource().policies();

        final var policy = policiesResource.findByName(name);
        if (Objects.nonNull(policy)) {
            log.warn("Policy {} for tenant {} already exists", name, tenantId);
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
                log.info("Created policy {} for tenant {}", name, tenantId);
                return createdResource;
            } else {
                throw new InternalServerErrorException("Failed to create tenant policy");
            }
        }
    }

    private ScopePermissionRepresentation createPermissionIfAny(final Long tenantId,
                                                                final String name,
                                                                final ResourceRepresentation resource,
                                                                final String scope,
                                                                final Set<PolicyRepresentation> policies) {
        final var permissionsResource = authzService.getAuthorizationResource().permissions().scope();

        final var permission = permissionsResource.findByName(name);
        if (Objects.nonNull(permission)) {
            log.warn("Permission {} for tenant {} already exists", name, tenantId);
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
                log.info("Created permission {} for tenant {}", name, tenantId);
                return createdResource;
            } else {
                throw new InternalServerErrorException("Failed to create tenant permission");
            }
        }
    }
}
