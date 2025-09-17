package com.omgservers.omgservice.project;

import com.omgservers.omgservice.authz.AuthzScope;
import com.omgservers.omgservice.authz.KeycloakService;
import jakarta.enterprise.context.ApplicationScoped;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class ProjectAuthzService {

    static private final String TENANT_ID_ATTRIBUTE = "tenant_id";
    static private final String PROJECT_ID_ATTRIBUTE = "project_id";

    final KeycloakService keycloakService;

    public ProjectAuthzService(final KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    public String getResourceName(final Long projectId) {
        return "resource:omg:project:%d".formatted(projectId);
    }

    public String getResourceType() {
        return "type:omg:project";
    }

    public ResourceRepresentation createResource(final Long tenantId, final Long projectId) {
        final var name = getResourceName(projectId);

        return keycloakService.createResource(name,
                getResourceType(),
                "Project %d".formatted(projectId),
                Set.of("/{ver}/project/%d/*".formatted(projectId)),
                AuthzScope.ALL.getMethods(),
                Map.of(TENANT_ID_ATTRIBUTE, List.of(tenantId.toString()),
                        PROJECT_ID_ATTRIBUTE, List.of(projectId.toString())));
    }

    public String getViewersGroupName(final Long projectId) {
        return "group:omg:project:%d:viewers".formatted(projectId);
    }

    public GroupRepresentation createViewersGroup(final Long projectId) {
        final var name = getViewersGroupName(projectId);
        return keycloakService.createGroup(name);
    }

    public String getManagersGroupName(final Long projectId) {
        return "group:omg:project:%d:managers".formatted(projectId);
    }

    public GroupRepresentation createManagersGroup(final Long projectId) {
        final var name = getManagersGroupName(projectId);
        return keycloakService.createGroup(name);
    }

    public String getAdminsGroupName(final Long projectId) {
        return "group:omg:project:%d:admins".formatted(projectId);
    }

    public GroupRepresentation createAdminsGroup(final Long projectId) {
        final var name = getAdminsGroupName(projectId);
        return keycloakService.createGroup(name);
    }

    public String getViewersPolicyName(final Long projectId) {
        return "policy:omg:project:%d:viewers".formatted(projectId);
    }

    public PolicyRepresentation createViewersPolicy(final Long projectId,
                                                    final GroupRepresentation viewersGroup) {
        final var name = getViewersPolicyName(projectId);
        return keycloakService.createPolicy(name, Set.of(viewersGroup));
    }

    public String getManagersPolicyName(final Long projectId) {
        return "policy:omg:project:%d:managers".formatted(projectId);
    }

    public PolicyRepresentation createManagersPolicy(final Long projectId,
                                                     final GroupRepresentation managersGroup) {
        final var name = getManagersPolicyName(projectId);
        return keycloakService.createPolicy(name, Set.of(managersGroup));
    }

    public String getAdminsPolicyName(final Long projectId) {
        return "policy:omg:project:%d:admins".formatted(projectId);
    }

    public PolicyRepresentation createAdminsPolicy(final Long projectId,
                                                   final GroupRepresentation adminsGroup) {
        final var name = getAdminsPolicyName(projectId);
        return keycloakService.createPolicy(name, Set.of(adminsGroup));
    }

    public String getViewPermissionName(final Long projectId) {
        return "permission:omg:project:%d:view".formatted(projectId);
    }

    public ScopePermissionRepresentation createViewPermission(final Long projectId,
                                                              final ResourceRepresentation resource,
                                                              final Set<PolicyRepresentation> policies) {
        final var name = getViewPermissionName(projectId);
        return keycloakService.createPermission(name, resource, AuthzScope.VIEW.getMethods(), policies);
    }

    public String getManagePermissionName(final Long projectId) {
        return "permission:omg:project:%d:manage".formatted(projectId);
    }

    public ScopePermissionRepresentation createManagePermission(final Long projectId,
                                                                final ResourceRepresentation resource,
                                                                final Set<PolicyRepresentation> policies) {
        final var name = getManagePermissionName(projectId);
        return keycloakService.createPermission(name, resource, AuthzScope.MANAGE.getMethods(), policies);
    }

    public String getAdminPermissionName(final Long projectId) {
        return "permission:omg:project:%d:admin".formatted(projectId);
    }

    public ScopePermissionRepresentation createAdminPermission(final Long projectId,
                                                               final ResourceRepresentation resource,
                                                               final Set<PolicyRepresentation> policies) {
        final var name = getAdminPermissionName(projectId);
        return keycloakService.createPermission(name, resource, AuthzScope.ADMIN.getMethods(), policies);
    }
}
