package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.authz.AuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class TenantAuthzService {
    static final Logger log = LoggerFactory.getLogger(TenantAuthzService.class);

    static private final String TENANT_VIEW_SCOPE = "urn:omg:tenant:view";
    static private final String TENANT_MANAGE_SCOPE = "urn:omg:tenant:manage";
    static private final String TENANT_SECURE_SCOPE = "urn:omg:tenant:secure";
    static private final String TENANT_DELETE_SCOPE = "urn:omg:tenant:delete";

    static private final String TENANT_RESOURCE_NAME = "urn:omg:tenant:%d";
    static private final String TENANT_RESOURCE_DISPLAY_NAME = "Tenant %d";
    static private final String TENANT_RESOURCE_TYPE = "urn:omg:tenants";
    static private final String TENANT_RESOURCE_URI = "/tenant/%d";

    static private final String TENANT_VIEWERS_GROUP = "urn:omg:tenant:%d:viewers";
    static private final String TENANT_MANAGERS_GROUP = "urn:omg:tenant:%d:managers";
    static private final String TENANT_OWNERS_GROUP = "urn:omg:tenant:%d:owners";

    static private final String TENANT_VIEWERS_POLICY_NAME = "Tenant %d";
    static private final String TENANT_VIEWERS_POLICY_DISPLAY_NAME = "urn:omg:tenant:%d:viewers";
    static private final String TENANT_MANAGERS_POLICY_NAME = "urn:omg:tenant:%d:managers";
    static private final String TENANT_OWNERS_POLICY_NAME = "urn:omg:tenant:%d:owners";

    static private final String TENANT_ATTRIBUTE_TENANT_ID = "tenant_id";

    final AuthzService authzService;

    public TenantAuthzService(final AuthzService authzService) {
        this.authzService = authzService;
    }

    public ResourceRepresentation createResourceIfAny(final Long tenantId) {
        final var resourcesResource = authzService.getAuthorizationResource().resources();

        final var name = TENANT_RESOURCE_NAME.formatted(tenantId);

        final var resources = resourcesResource.findByName(name);
        if (!resources.isEmpty()) {
            log.warn("Resource for tenant {} already exists", tenantId);
            return resources.getFirst();
        }

        final var representation = new ResourceRepresentation();
        representation.setName(name);
        representation.setType(TENANT_RESOURCE_TYPE);
        representation.setDisplayName(TENANT_RESOURCE_DISPLAY_NAME.formatted(tenantId));
        representation.setUris(Set.of(TENANT_RESOURCE_URI.formatted(tenantId)));
        representation.addScope(new ScopeRepresentation(TENANT_VIEW_SCOPE));
        representation.addScope(new ScopeRepresentation(TENANT_MANAGE_SCOPE));
        representation.addScope(new ScopeRepresentation(TENANT_SECURE_SCOPE));
        representation.addScope(new ScopeRepresentation(TENANT_DELETE_SCOPE));
        representation.setAttributes(Map.of(TENANT_ATTRIBUTE_TENANT_ID, List.of(tenantId.toString())));

        try (final var response = resourcesResource.create(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdResource = resourcesResource.findByName(name).getFirst();
                log.info("Created resource {} for tenant {}", name, tenantId);
                return createdResource;
            } else {
                throw new InternalServerErrorException("Failed to create tenant resource");
            }
        }
    }

    public GroupRepresentation createViewersGroupIfAny(final Long tenantId) {
        final var name = TENANT_VIEWERS_GROUP.formatted(tenantId);
        return createGroupIfAny(tenantId, name);
    }

    public GroupRepresentation createManagersGroupIfAny(final Long tenantId) {
        final var name = TENANT_MANAGERS_GROUP.formatted(tenantId);
        return createGroupIfAny(tenantId, name);
    }

    public GroupRepresentation createOwnersGroupIfAny(final Long tenantId) {
        final var name = TENANT_OWNERS_GROUP.formatted(tenantId);
        return createGroupIfAny(tenantId, name);
    }

    private GroupRepresentation createGroupIfAny(final Long tenantId, final String name) {
        final var groupsResource = authzService.getGroupsResource();

        final var groups = groupsResource.groups(TENANT_VIEWERS_GROUP, 0, 1);
        if (!groups.isEmpty()) {
            log.warn("Group {} for tenant {} already exists", name, tenantId);
            return groups.getFirst();
        }

        final var representation = new GroupRepresentation();
        representation.setName(name);

        try (final var response = groupsResource.add(representation)) {
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                final var createdGroup = groupsResource.groups(name, 0, 1).getFirst();
                log.info("Created group {} for tenant {}", name, tenantId);

                return createdGroup;
            } else {
                throw new InternalServerErrorException("Failed to create tenant group");
            }
        }
    }
}
