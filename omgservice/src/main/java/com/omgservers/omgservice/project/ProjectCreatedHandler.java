package com.omgservers.omgservice.project;

import com.omgservers.omgservice.authz.AuthzService;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.tenant.TenantAuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class ProjectCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCreatedHandler.class);

    final ProjectAuthzService projectAuthzService;
    final TenantAuthzService tenantAuthzService;
    final ProjectService projectService;
    final AuthzService authzService;

    public ProjectCreatedHandler(final ProjectAuthzService projectAuthzService,
                                 final TenantAuthzService tenantAuthzService,
                                 final ProjectService projectService,
                                 final AuthzService authzService) {
        this.projectAuthzService = projectAuthzService;
        this.tenantAuthzService = tenantAuthzService;
        this.projectService = projectService;
        this.authzService = authzService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.PROJECT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var project = Project.findByIdRequired(resourceId);
        final var tenantId = project.tenant.id;

        LOGGER.info("Creating project {}", resourceId);

        final var viewersGroup = projectAuthzService.createViewersGroup(resourceId);
        final var developersGroup = projectAuthzService.createDevelopersGroup(resourceId);
        final var managersGroup = projectAuthzService.createManagersGroup(resourceId);
        final var adminsGroup = projectAuthzService.createAdminsGroup(resourceId);

        final var projectResource = projectAuthzService.createResource(tenantId, resourceId);

        final var projectViewersPolicy = projectAuthzService.createViewersPolicy(resourceId, viewersGroup);
        final var projectDevelopersPolicy = projectAuthzService.createDevelopersPolicy(resourceId, developersGroup);
        final var projectManagersPolicy = projectAuthzService.createManagersPolicy(resourceId, managersGroup);
        final var projectAdminsPolicy = projectAuthzService.createAdminsPolicy(resourceId, adminsGroup);

        final var tenantViewersPolicyName = tenantAuthzService.getViewersPolicyName(tenantId);
        final var tenantManagersPolicyName = tenantAuthzService.getManagersPolicyName(tenantId);
        final var tenantAdminsPolicyName = tenantAuthzService.getAdminsPolicyName(tenantId);

        final var tenantViewersPolicy = authzService.findPolicyByNameRequired(tenantViewersPolicyName);
        final var tenantManagersPolicy = authzService.findPolicyByNameRequired(tenantManagersPolicyName);
        final var tenantAdminsPolicy = authzService.findPolicyByNameRequired(tenantAdminsPolicyName);

        final var viewPermissionPolicies = Set.of(projectViewersPolicy,
                projectDevelopersPolicy,
                projectManagersPolicy,
                projectAdminsPolicy,
                tenantViewersPolicy,
                tenantManagersPolicy,
                tenantAdminsPolicy);
        projectAuthzService.createViewPermission(resourceId, projectResource, viewPermissionPolicies);

        final var developPermissionPolicies = Set.of(projectDevelopersPolicy,
                projectAdminsPolicy,
                tenantAdminsPolicy);
        projectAuthzService.createDevelopPermission(resourceId, projectResource, developPermissionPolicies);

        final var managePermissionPolicies = Set.of(projectManagersPolicy,
                projectAdminsPolicy,
                tenantManagersPolicy,
                tenantAdminsPolicy);
        projectAuthzService.createManagePermission(resourceId, projectResource, managePermissionPolicies);

        final var adminPermissionPolicies = Set.of(projectAdminsPolicy, tenantAdminsPolicy);
        projectAuthzService.createAdminPermission(resourceId, projectResource, adminPermissionPolicies);

        projectService.switchStateFromCreatingToCreated(resourceId);
    }
}
