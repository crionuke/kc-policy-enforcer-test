package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.authz.AuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class TenantAuthzService {

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
        final var name = RESOURCE_NAME.formatted(tenantId);

        return authzService.createResourceIfAny(tenantId,
                name,
                RESOURCE_TYPE,
                RESOURCE_DISPLAY_NAME.formatted(tenantId),
                RESOURCE_URI.formatted(tenantId),
                Set.of(VIEW_SCOPE, MANAGE_SCOPE, ADMIN_SCOPE),
                Map.of(TENANT_ATTRIBUTE_TENANT_ID, List.of(tenantId.toString())));
    }

    public GroupRepresentation createViewersGroupIfAny(final Long tenantId) {
        final var name = VIEWERS_GROUP_NAME.formatted(tenantId);
        return authzService.createGroupIfAny(tenantId, name);
    }

    public GroupRepresentation createManagersGroupIfAny(final Long tenantId) {
        final var name = MANAGERS_GROUP_NAME.formatted(tenantId);
        return authzService.createGroupIfAny(tenantId, name);
    }

    public GroupRepresentation createAdminsGroupIfAny(final Long tenantId) {
        final var name = ADMINS_GROUP_NAME.formatted(tenantId);
        return authzService.createGroupIfAny(tenantId, name);
    }

    public PolicyRepresentation createViewersPolicyIfAny(final Long tenantId,
                                                         final GroupRepresentation viewersGroup) {
        final var name = VIEWERS_POLICY_NAME.formatted(tenantId);
        return authzService.createPolicyIfAny(tenantId, name, Set.of(viewersGroup));
    }

    public PolicyRepresentation createManagersPolicyIfAny(final Long tenantId,
                                                          final GroupRepresentation managersGroup) {
        final var name = MANAGERS_POLICY_NAME.formatted(tenantId);
        return authzService.createPolicyIfAny(tenantId, name, Set.of(managersGroup));
    }

    public PolicyRepresentation createAdminsPolicyIfAny(final Long tenantId,
                                                        final GroupRepresentation adminsGroup) {
        final var name = ADMINS_POLICY_NAME.formatted(tenantId);
        return authzService.createPolicyIfAny(tenantId, name, Set.of(adminsGroup));
    }

    public ScopePermissionRepresentation createViewPermissionIfAny(final Long tenantId,
                                                                   final ResourceRepresentation resource,
                                                                   final Set<PolicyRepresentation> policies) {
        final var name = VIEW_PERMISSION_NAME.formatted(tenantId);
        return authzService.createPermissionIfAny(tenantId, name, resource, VIEW_SCOPE, policies);
    }

    public ScopePermissionRepresentation createManagePermissionIfAny(final Long tenantId,
                                                                     final ResourceRepresentation resource,
                                                                     final Set<PolicyRepresentation> policies) {
        final var name = MANAGE_PERMISSION_NAME.formatted(tenantId);
        return authzService.createPermissionIfAny(tenantId, name, resource, MANAGE_SCOPE, policies);
    }

    public ScopePermissionRepresentation createAdminPermissionIfAny(final Long tenantId,
                                                                    final ResourceRepresentation resource,
                                                                    final Set<PolicyRepresentation> policies) {
        final var name = ADMIN_PERMISSION_NAME.formatted(tenantId);
        return authzService.createPermissionIfAny(tenantId, name, resource, ADMIN_SCOPE, policies);
    }
}
