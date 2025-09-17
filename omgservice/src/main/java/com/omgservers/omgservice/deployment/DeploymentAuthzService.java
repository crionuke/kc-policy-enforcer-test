package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.authz.AuthzScope;
import com.omgservers.omgservice.authz.KeycloakService;
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

    final KeycloakService keycloakService;

    public DeploymentAuthzService(final KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
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

        return keycloakService.createResource(name,
                getResourceType(),
                "Deployment %d".formatted(deploymentId),
                Set.of("/{ver}/deployment/%d/*".formatted(deploymentId)),
                AuthzScope.ALL.getMethods(),
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
        return keycloakService.createPermission(name, resource, AuthzScope.VIEW.getMethods(), policies);
    }

    public String getManagePermissionName(final Long deploymentId) {
        return "permission:omg:deployment:%d:manage".formatted(deploymentId);
    }

    public ScopePermissionRepresentation createManagePermission(final Long deploymentId,
                                                                final ResourceRepresentation resource,
                                                                final Set<PolicyRepresentation> policies) {
        final var name = getManagePermissionName(deploymentId);
        return keycloakService.createPermission(name, resource, AuthzScope.MANAGE.getMethods(), policies);
    }

    public String getAdminPermissionName(final Long deploymentId) {
        return "permission:omg:deployment:%d:admin".formatted(deploymentId);
    }

    public ScopePermissionRepresentation createAdminPermission(final Long deploymentId,
                                                               final ResourceRepresentation resource,
                                                               final Set<PolicyRepresentation> policies) {
        final var name = getAdminPermissionName(deploymentId);
        return keycloakService.createPermission(name, resource, AuthzScope.ADMIN.getMethods(), policies);
    }
}
