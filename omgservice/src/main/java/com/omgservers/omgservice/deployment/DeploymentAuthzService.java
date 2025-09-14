package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.authz.AuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class DeploymentAuthzService {

    static private final String TENANT_ID_ATTRIBUTE = "tenant_id";
    static private final String STAGE_ID_ATTRIBUTE = "stage_id";
    static private final String DEPLOYMENT_ID_ATTRIBUTE = "deployment_id";

    final AuthzService authzService;

    public DeploymentAuthzService(final AuthzService authzService) {
        this.authzService = authzService;
    }

    public String getResourceName(final Long deploymentId) {
        return "resource:omg:deployment:%d".formatted(deploymentId);
    }

    public String getResourceType() {
        return "type:omg:deployment";
    }

    public ResourceRepresentation createResource(final Long tenantId,
                                                 final Long stageId,
                                                 final Long deploymentId) {
        final var name = getResourceName(deploymentId);

        final var scopeNames = Set.of(DeploymentScope.VIEW.getName(),
                DeploymentScope.MANAGE.getName(),
                DeploymentScope.ADMIN.getName());

        return authzService.createResource(name,
                getResourceType(),
                "Deployment %d".formatted(deploymentId),
                Set.of("/deployment/%d/*".formatted(deploymentId)),
                scopeNames,
                Map.of(TENANT_ID_ATTRIBUTE, List.of(tenantId.toString()),
                        STAGE_ID_ATTRIBUTE, List.of(stageId.toString()),
                        DEPLOYMENT_ID_ATTRIBUTE, List.of(deploymentId.toString())));
    }

    public String getViewPermissionName(final Long deploymentId) {
        return "permission:omg:deployment:%d:view".formatted(deploymentId);
    }

    public ScopePermissionRepresentation createViewPermission(final Long deploymentId,
                                                              final ResourceRepresentation resource,
                                                              final Set<PolicyRepresentation> policies) {
        final var name = getViewPermissionName(deploymentId);
        return authzService.createPermission(name, resource, DeploymentScope.VIEW.getName(), policies);
    }

    public String getManagePermissionName(final Long deploymentId) {
        return "permission:omg:deployment:%d:manage".formatted(deploymentId);
    }

    public ScopePermissionRepresentation createManagePermission(final Long deploymentId,
                                                                final ResourceRepresentation resource,
                                                                final Set<PolicyRepresentation> policies) {
        final var name = getManagePermissionName(deploymentId);
        return authzService.createPermission(name, resource, DeploymentScope.MANAGE.getName(), policies);
    }

    public String getAdminPermissionName(final Long deploymentId) {
        return "permission:omg:deployment:%d:admin".formatted(deploymentId);
    }

    public ScopePermissionRepresentation createAdminPermission(final Long deploymentId,
                                                               final ResourceRepresentation resource,
                                                               final Set<PolicyRepresentation> policies) {
        final var name = getAdminPermissionName(deploymentId);
        return authzService.createPermission(name, resource, DeploymentScope.ADMIN.getName(), policies);
    }
}
