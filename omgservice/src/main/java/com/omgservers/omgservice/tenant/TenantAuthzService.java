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

    static private final String TENANT_ID_ATTRIBUTE = "tenant_id";

    final AuthzService authzService;

    public TenantAuthzService(final AuthzService authzService) {
        this.authzService = authzService;
    }

    public String getResourceName(final Long tenantId) {
        return "resource:omg:tenant:%d".formatted(tenantId);
    }

    public String getResourceType() {
        return "type:omg:tenant";
    }

    public ResourceRepresentation createResource(final Long tenantId) {
        final var name = getResourceName(tenantId);

        final var scopeNames = Set.of(TenantScope.VIEW.getName(),
                TenantScope.MANAGE.getName(),
                TenantScope.ADMIN.getName());

        return authzService.createResource(name,
                getResourceType(),
                "Tenant %d".formatted(tenantId),
                "/tenant/%d".formatted(tenantId),
                scopeNames,
                Map.of(TENANT_ID_ATTRIBUTE, List.of(tenantId.toString())));
    }

    public String getViewersGroupName(final Long tenantId) {
        return "group:omg:tenant:%d:viewers".formatted(tenantId);
    }

    public GroupRepresentation createViewersGroup(final Long tenantId) {
        final var name = getViewersGroupName(tenantId);
        return authzService.createGroup(name);
    }

    public String getManagersGroupName(final Long tenantId) {
        return "group:omg:tenant:%d:managers".formatted(tenantId);
    }

    public GroupRepresentation createManagersGroup(final Long tenantId) {
        final var name = getManagersGroupName(tenantId);
        return authzService.createGroup(name);
    }

    public String getAdminsGroupName(final Long tenantId) {
        return "group:omg:tenant:%d:admins".formatted(tenantId);
    }

    public GroupRepresentation createAdminsGroup(final Long tenantId) {
        final var name = getAdminsGroupName(tenantId);
        return authzService.createGroup(name);
    }

    public String getViewersPolicyName(final Long tenantId) {
        return "policy:omg:tenant:%d:viewers".formatted(tenantId);
    }

    public PolicyRepresentation createViewersPolicy(final Long tenantId,
                                                    final GroupRepresentation viewersGroup) {
        final var name = getViewersPolicyName(tenantId);
        return authzService.createPolicy(name, Set.of(viewersGroup));
    }

    public String getManagersPolicyName(final Long tenantId) {
        return "policy:omg:tenant:%d:managers".formatted(tenantId);
    }

    public PolicyRepresentation createManagersPolicy(final Long tenantId,
                                                     final GroupRepresentation managersGroup) {
        final var name = getManagersPolicyName(tenantId);
        return authzService.createPolicy(name, Set.of(managersGroup));
    }

    public String getAdminsPolicyName(final Long tenantId) {
        return "policy:omg:tenant:%d:admins".formatted(tenantId);
    }

    public PolicyRepresentation createAdminsPolicy(final Long tenantId,
                                                   final GroupRepresentation adminsGroup) {
        final var name = getAdminsPolicyName(tenantId);
        return authzService.createPolicy(name, Set.of(adminsGroup));
    }

    public String getViewPermissionName(final Long tenantId) {
        return "permission:omg:tenant:%d:view".formatted(tenantId);
    }

    public ScopePermissionRepresentation createViewPermission(final Long tenantId,
                                                              final ResourceRepresentation resource,
                                                              final Set<PolicyRepresentation> policies) {
        final var name = getViewPermissionName(tenantId);
        return authzService.createPermission(name, resource, TenantScope.VIEW.getName(), policies);
    }

    public String getManagePermissionName(final Long tenantId) {
        return "permission:omg:tenant:%d:manage".formatted(tenantId);
    }

    public ScopePermissionRepresentation createManagePermission(final Long tenantId,
                                                                final ResourceRepresentation resource,
                                                                final Set<PolicyRepresentation> policies) {
        final var name = getManagePermissionName(tenantId);
        return authzService.createPermission(name, resource, TenantScope.MANAGE.getName(), policies);
    }

    public String getAdminPermissionName(final Long tenantId) {
        return "permission:omg:tenant:%d:admin".formatted(tenantId);
    }

    public ScopePermissionRepresentation createAdminPermission(final Long tenantId,
                                                               final ResourceRepresentation resource,
                                                               final Set<PolicyRepresentation> policies) {
        final var name = getAdminPermissionName(tenantId);
        return authzService.createPermission(name, resource, TenantScope.ADMIN.getName(), policies);
    }
}
