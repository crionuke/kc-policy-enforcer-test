package com.omgservers.omgservice.version;

import com.omgservers.omgservice.authz.AuthzService;
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

    final AuthzService authzService;

    public VersionAuthzService(final AuthzService authzService) {
        this.authzService = authzService;
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

        final var scopeNames = Set.of(VersionScope.VIEW.getName(),
                VersionScope.MANAGE.getName(),
                VersionScope.ADMIN.getName());

        return authzService.createResource(name,
                getResourceType(),
                "Version %d".formatted(versionId),
                Set.of("/version/%d/*".formatted(versionId)),
                scopeNames,
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
        return authzService.createPermission(name, resource, VersionScope.VIEW.getName(), policies);
    }

    public String getManagePermissionName(final Long versionId) {
        return "permission:omg:version:%d:manage".formatted(versionId);
    }

    public ScopePermissionRepresentation createManagePermission(final Long versionId,
                                                                final ResourceRepresentation resource,
                                                                final Set<PolicyRepresentation> policies) {
        final var name = getManagePermissionName(versionId);
        return authzService.createPermission(name, resource, VersionScope.MANAGE.getName(), policies);
    }

    public String getAdminPermissionName(final Long versionId) {
        return "permission:omg:version:%d:admin".formatted(versionId);
    }

    public ScopePermissionRepresentation createAdminPermission(final Long versionId,
                                                               final ResourceRepresentation resource,
                                                               final Set<PolicyRepresentation> policies) {
        final var name = getAdminPermissionName(versionId);
        return authzService.createPermission(name, resource, VersionScope.ADMIN.getName(), policies);
    }
}
