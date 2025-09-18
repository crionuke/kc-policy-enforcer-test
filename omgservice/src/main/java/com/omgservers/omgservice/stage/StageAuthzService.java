package com.omgservers.omgservice.stage;

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
public class StageAuthzService {

    static private final String TENANT_ID_ATTRIBUTE = "tenant_id";
    static private final String STAGE_ID_ATTRIBUTE = "stage_id";

    final KeycloakService keycloakService;

    public StageAuthzService(final KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    public String getResourceName(final Long stageId) {
        return "resource:omg:stage:%d".formatted(stageId);
    }

    public String getResourceType() {
        return "type:omg:stage";
    }

    public ResourceRepresentation createResource(final Long tenantId, final Long stageId) {
        final var name = getResourceName(stageId);

        return keycloakService.createResource(name,
                getResourceType(),
                "Stage %d".formatted(stageId),
                Set.of("/{ver}/stages/%d/*".formatted(stageId)),
                AuthzScope.ALL.getMethods(),
                Map.of(TENANT_ID_ATTRIBUTE, List.of(tenantId.toString()),
                        STAGE_ID_ATTRIBUTE, List.of(stageId.toString())));
    }

    public String getViewersGroupName(final Long stageId) {
        return "group:omg:stage:%d:viewers".formatted(stageId);
    }

    public GroupRepresentation createViewersGroup(final Long stageId) {
        final var name = getViewersGroupName(stageId);
        return keycloakService.createGroup(name);
    }

    public String getManagersGroupName(final Long stageId) {
        return "group:omg:stage:%d:managers".formatted(stageId);
    }

    public GroupRepresentation createManagersGroup(final Long stagId) {
        final var name = getManagersGroupName(stagId);
        return keycloakService.createGroup(name);
    }

    public String getAdminsGroupName(final Long stageId) {
        return "group:omg:stage:%d:admins".formatted(stageId);
    }

    public GroupRepresentation createAdminsGroup(final Long stageId) {
        final var name = getAdminsGroupName(stageId);
        return keycloakService.createGroup(name);
    }

    public String getViewersPolicyName(final Long stageId) {
        return "policy:omg:stage:%d:viewers".formatted(stageId);
    }

    public PolicyRepresentation createViewersPolicy(final Long stageId,
                                                    final GroupRepresentation viewersGroup) {
        final var name = getViewersPolicyName(stageId);
        return keycloakService.createPolicy(name, Set.of(viewersGroup));
    }

    public String getManagersPolicyName(final Long stageId) {
        return "policy:omg:stage:%d:managers".formatted(stageId);
    }

    public PolicyRepresentation createManagersPolicy(final Long stageId,
                                                     final GroupRepresentation managersGroup) {
        final var name = getManagersPolicyName(stageId);
        return keycloakService.createPolicy(name, Set.of(managersGroup));
    }

    public String getAdminsPolicyName(final Long stageId) {
        return "policy:omg:stage:%d:admins".formatted(stageId);
    }

    public PolicyRepresentation createAdminsPolicy(final Long stageId,
                                                   final GroupRepresentation adminsGroup) {
        final var name = getAdminsPolicyName(stageId);
        return keycloakService.createPolicy(name, Set.of(adminsGroup));
    }

    public String getViewPermissionName(final Long stageId) {
        return "permission:omg:stage:%d:view".formatted(stageId);
    }

    public ScopePermissionRepresentation createViewPermission(final Long stageId,
                                                              final ResourceRepresentation resource,
                                                              final Set<PolicyRepresentation> policies) {
        final var name = getViewPermissionName(stageId);
        return keycloakService.createPermission(name, resource, AuthzScope.VIEW.getMethods(), policies);
    }

    public String getManagePermissionName(final Long stageId) {
        return "permission:omg:stage:%d:manage".formatted(stageId);
    }

    public ScopePermissionRepresentation createManagePermission(final Long stageId,
                                                                final ResourceRepresentation resource,
                                                                final Set<PolicyRepresentation> policies) {
        final var name = getManagePermissionName(stageId);
        return keycloakService.createPermission(name, resource, AuthzScope.MANAGE.getMethods(), policies);
    }

    public String getAdminPermissionName(final Long stageId) {
        return "permission:omg:stage:%d:admin".formatted(stageId);
    }

    public ScopePermissionRepresentation createAdminPermission(final Long stageId,
                                                               final ResourceRepresentation resource,
                                                               final Set<PolicyRepresentation> policies) {
        final var name = getAdminPermissionName(stageId);
        return keycloakService.createPermission(name, resource, AuthzScope.ADMIN.getMethods(), policies);
    }
}
