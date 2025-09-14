package com.omgservers.omgservice.version;

import com.omgservers.omgservice.authz.AuthzService;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.project.ProjectAuthzService;
import com.omgservers.omgservice.tenant.TenantAuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class VersionCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionCreatedHandler.class);

    final ProjectAuthzService projectAuthzService;
    final VersionAuthzService versionAuthzService;
    final TenantAuthzService tenantAuthzService;
    final VersionService versionService;
    final AuthzService authzService;

    public VersionCreatedHandler(final ProjectAuthzService projectAuthzService,
                                 final VersionAuthzService versionAuthzService,
                                 final TenantAuthzService tenantAuthzService,
                                 final VersionService versionService,
                                 final AuthzService authzService) {
        this.projectAuthzService = projectAuthzService;
        this.versionAuthzService = versionAuthzService;
        this.tenantAuthzService = tenantAuthzService;
        this.versionService = versionService;
        this.authzService = authzService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.VERSION_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var version = Version.findByIdRequired(resourceId);
        final var tenantId = version.project.tenant.id;
        final var projectId = version.project.id;

        LOGGER.info("Creating version {}", resourceId);

        final var versionResources = versionAuthzService.createResource(tenantId, projectId, resourceId);

        final var tenantViewersPolicyName = tenantAuthzService.getViewersPolicyName(tenantId);
        final var tenantManagersPolicyName = tenantAuthzService.getManagersPolicyName(tenantId);
        final var tenantAdminsPolicyName = tenantAuthzService.getAdminsPolicyName(tenantId);

        final var tenantViewersPolicy = authzService.findPolicyByNameRequired(tenantViewersPolicyName);
        final var tenantManagersPolicy = authzService.findPolicyByNameRequired(tenantManagersPolicyName);
        final var tenantAdminsPolicy = authzService.findPolicyByNameRequired(tenantAdminsPolicyName);

        final var projectViewersPolicyName = projectAuthzService.getViewersPolicyName(projectId);
        final var projectManagersPolicyName = projectAuthzService.getManagersPolicyName(projectId);
        final var projectAdminsPolicyName = projectAuthzService.getAdminsPolicyName(projectId);

        final var projectViewersPolicy = authzService.findPolicyByNameRequired(projectViewersPolicyName);
        final var projectManagersPolicy = authzService.findPolicyByNameRequired(projectManagersPolicyName);
        final var projectAdminsPolicy = authzService.findPolicyByNameRequired(projectAdminsPolicyName);

        final var viewPermissionPolicies = Set.of(projectViewersPolicy,
                projectManagersPolicy,
                projectAdminsPolicy,
                tenantViewersPolicy,
                tenantManagersPolicy,
                tenantAdminsPolicy);
        versionAuthzService.createViewPermission(resourceId, versionResources, viewPermissionPolicies);

        final var managePermissionPolicies = Set.of(projectManagersPolicy,
                projectAdminsPolicy,
                tenantManagersPolicy,
                tenantAdminsPolicy);
        versionAuthzService.createManagePermission(resourceId, versionResources, managePermissionPolicies);

        final var adminPermissionPolicies = Set.of(projectAdminsPolicy, tenantAdminsPolicy);
        versionAuthzService.createAdminPermission(resourceId, versionResources, adminPermissionPolicies);

        versionService.switchStateFromCreatingToCreated(resourceId);
    }
}
