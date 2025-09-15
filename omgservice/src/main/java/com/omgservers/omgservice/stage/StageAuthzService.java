package com.omgservers.omgservice.stage;

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
public class StageAuthzService {

    static private final String TENANT_ID_ATTRIBUTE = "tenant_id";
    static private final String STAGE_ID_ATTRIBUTE = "stage_id";

    final AuthzService authzService;

    public StageAuthzService(final AuthzService authzService) {
        this.authzService = authzService;
    }

    public String getResourceName(final Long stageId) {
        return "resource:omg:stage:%d".formatted(stageId);
    }

    public String getResourceType() {
        return "type:omg:stage";
    }

    public ResourceRepresentation createResource(final Long tenantId, final Long stageId) {
        final var name = getResourceName(stageId);

        final var scopeNames = Set.of(StageScope.VIEW.getName(),
                StageScope.MANAGE.getName(),
                StageScope.ADMIN.getName());

        return authzService.createResource(name,
                getResourceType(),
                "Stage %d".formatted(stageId),
                Set.of("/{v}/stage/%d/*".formatted(stageId)),
                scopeNames,
                Map.of(TENANT_ID_ATTRIBUTE, List.of(tenantId.toString()),
                        STAGE_ID_ATTRIBUTE, List.of(stageId.toString())));
    }

    public String getViewersGroupName(final Long stageId) {
        return "group:omg:stage:%d:viewers".formatted(stageId);
    }

    public GroupRepresentation createViewersGroup(final Long stageId) {
        final var name = getViewersGroupName(stageId);
        return authzService.createGroup(name);
    }

    public String getManagersGroupName(final Long stageId) {
        return "group:omg:stage:%d:managers".formatted(stageId);
    }

    public GroupRepresentation createManagersGroup(final Long stagId) {
        final var name = getManagersGroupName(stagId);
        return authzService.createGroup(name);
    }

    public String getAdminsGroupName(final Long stageId) {
        return "group:omg:stage:%d:admins".formatted(stageId);
    }

    public GroupRepresentation createAdminsGroup(final Long stageId) {
        final var name = getAdminsGroupName(stageId);
        return authzService.createGroup(name);
    }

    public String getViewersPolicyName(final Long stageId) {
        return "policy:omg:stage:%d:viewers".formatted(stageId);
    }

    public PolicyRepresentation createViewersPolicy(final Long stageId,
                                                    final GroupRepresentation viewersGroup) {
        final var name = getViewersPolicyName(stageId);
        return authzService.createPolicy(name, Set.of(viewersGroup));
    }

    public String getManagersPolicyName(final Long stageId) {
        return "policy:omg:stage:%d:managers".formatted(stageId);
    }

    public PolicyRepresentation createManagersPolicy(final Long stageId,
                                                     final GroupRepresentation managersGroup) {
        final var name = getManagersPolicyName(stageId);
        return authzService.createPolicy(name, Set.of(managersGroup));
    }

    public String getAdminsPolicyName(final Long stageId) {
        return "policy:omg:stage:%d:admins".formatted(stageId);
    }

    public PolicyRepresentation createAdminsPolicy(final Long stageId,
                                                   final GroupRepresentation adminsGroup) {
        final var name = getAdminsPolicyName(stageId);
        return authzService.createPolicy(name, Set.of(adminsGroup));
    }

    public String getViewPermissionName(final Long stageId) {
        return "permission:omg:stage:%d:view".formatted(stageId);
    }

    public ScopePermissionRepresentation createViewPermission(final Long stageId,
                                                              final ResourceRepresentation resource,
                                                              final Set<PolicyRepresentation> policies) {
        final var name = getViewPermissionName(stageId);
        return authzService.createPermission(name, resource, StageScope.VIEW.getName(), policies);
    }

    public String getManagePermissionName(final Long stageId) {
        return "permission:omg:stage:%d:manage".formatted(stageId);
    }

    public ScopePermissionRepresentation createManagePermission(final Long stageId,
                                                                final ResourceRepresentation resource,
                                                                final Set<PolicyRepresentation> policies) {
        final var name = getManagePermissionName(stageId);
        return authzService.createPermission(name, resource, StageScope.MANAGE.getName(), policies);
    }

    public String getAdminPermissionName(final Long stageId) {
        return "permission:omg:stage:%d:admin".formatted(stageId);
    }

    public ScopePermissionRepresentation createAdminPermission(final Long stageId,
                                                               final ResourceRepresentation resource,
                                                               final Set<PolicyRepresentation> policies) {
        final var name = getAdminPermissionName(stageId);
        return authzService.createPermission(name, resource, StageScope.ADMIN.getName(), policies);
    }
}
