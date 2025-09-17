package com.omgservers.omgservice.version;

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
public class VersionAuthzService {

    static private final String TENANT_ID_ATTRIBUTE = "tenant_id";
    static private final String PROJECT_ID_ATTRIBUTE = "project_id";
    static private final String VERSION_ID_ATTRIBUTE = "version_id";

    final KeycloakService keycloakService;

    public VersionAuthzService(final KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    public String getResourceName(final Long versionId) {
        return "resource:omg:version:%d".formatted(versionId);
    }

    public String getResourceType() {
        return "type:omg:version";
    }

    public ResourceRepresentation createResource(final Long tenantId,
                                                 final Long projectId,
                                                 final Long versionId) {
        final var name = getResourceName(versionId);

        return keycloakService.createResource(name,
                getResourceType(),
                "Version %d".formatted(versionId),
                Set.of("/{ver}/version/%d/*".formatted(versionId)),
                AuthzScope.ALL.getMethods(),
                Map.of(TENANT_ID_ATTRIBUTE, List.of(tenantId.toString()),
                        PROJECT_ID_ATTRIBUTE, List.of(projectId.toString()),
                        VERSION_ID_ATTRIBUTE, List.of(versionId.toString())));
    }

    public String getViewPermissionName(final Long versionId) {
        return "permission:omg:version:%d:view".formatted(versionId);
    }

    public ScopePermissionRepresentation createViewPermission(final Long versionId,
                                                              final ResourceRepresentation resource,
                                                              final Set<PolicyRepresentation> policies) {
        final var name = getViewPermissionName(versionId);
        return keycloakService.createPermission(name, resource, AuthzScope.VIEW.getMethods(), policies);
    }

    public String getManagePermissionName(final Long versionId) {
        return "permission:omg:version:%d:manage".formatted(versionId);
    }

    public ScopePermissionRepresentation createManagePermission(final Long versionId,
                                                                final ResourceRepresentation resource,
                                                                final Set<PolicyRepresentation> policies) {
        final var name = getManagePermissionName(versionId);
        return keycloakService.createPermission(name, resource, AuthzScope.MANAGE.getMethods(), policies);
    }

    public String getAdminPermissionName(final Long versionId) {
        return "permission:omg:version:%d:admin".formatted(versionId);
    }

    public ScopePermissionRepresentation createAdminPermission(final Long versionId,
                                                               final ResourceRepresentation resource,
                                                               final Set<PolicyRepresentation> policies) {
        final var name = getAdminPermissionName(versionId);
        return keycloakService.createPermission(name, resource, AuthzScope.ADMIN.getMethods(), policies);
    }
}
