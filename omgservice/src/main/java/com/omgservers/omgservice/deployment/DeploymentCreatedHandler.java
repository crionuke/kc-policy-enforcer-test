package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.authz.AuthzService;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.project.ProjectAuthzService;
import com.omgservers.omgservice.stage.StageAuthzService;
import com.omgservers.omgservice.tenant.TenantAuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class DeploymentCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentCreatedHandler.class);

    final DeploymentAuthzService deploymentAuthzService;
    final ProjectAuthzService projectAuthzService;
    final TenantAuthzService tenantAuthzService;
    final StageAuthzService stageAuthzService;
    final DeploymentService deploymentService;
    final AuthzService authzService;

    public DeploymentCreatedHandler(final DeploymentAuthzService deploymentAuthzService,
                                    final ProjectAuthzService projectAuthzService,
                                    final TenantAuthzService tenantAuthzService,
                                    final StageAuthzService stageAuthzService,
                                    final DeploymentService deploymentService,
                                    final AuthzService authzService) {
        this.deploymentAuthzService = deploymentAuthzService;
        this.projectAuthzService = projectAuthzService;
        this.tenantAuthzService = tenantAuthzService;
        this.stageAuthzService = stageAuthzService;
        this.deploymentService = deploymentService;
        this.authzService = authzService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.DEPLOYMENT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var deployment = Deployment.findByIdRequired(resourceId);
        final var tenantId = deployment.stage.tenant.id;
        final var stageId = deployment.stage.id;
        final var projectId = deployment.version.project.id;

        LOGGER.info("Creating deployment {}", resourceId);

        final var versionResources = deploymentAuthzService.createResource(tenantId, stageId, resourceId);

        final var tenantViewersPolicyName = tenantAuthzService.getViewersPolicyName(tenantId);
        final var tenantManagersPolicyName = tenantAuthzService.getManagersPolicyName(tenantId);
        final var tenantAdminsPolicyName = tenantAuthzService.getAdminsPolicyName(tenantId);

        final var tenantViewersPolicy = authzService.findPolicyByNameRequired(tenantViewersPolicyName);
        final var tenantManagersPolicy = authzService.findPolicyByNameRequired(tenantManagersPolicyName);
        final var tenantAdminsPolicy = authzService.findPolicyByNameRequired(tenantAdminsPolicyName);

        final var stageViewersPolicyName = stageAuthzService.getViewersPolicyName(stageId);
        final var stageManagersPolicyName = stageAuthzService.getManagersPolicyName(stageId);
        final var stageAdminsPolicyName = stageAuthzService.getAdminsPolicyName(stageId);

        final var stageViewersPolicy = authzService.findPolicyByNameRequired(stageViewersPolicyName);
        final var stageManagersPolicy = authzService.findPolicyByNameRequired(stageManagersPolicyName);
        final var stageAdminsPolicy = authzService.findPolicyByNameRequired(stageAdminsPolicyName);

        final var projectViewersPolicyName = projectAuthzService.getViewersPolicyName(projectId);
        final var projectManagersPolicyName = projectAuthzService.getManagersPolicyName(projectId);
        final var projectAdminsPolicyName = projectAuthzService.getAdminsPolicyName(projectId);

        final var projectViewersPolicy = authzService.findPolicyByNameRequired(projectViewersPolicyName);
        final var projectManagersPolicy = authzService.findPolicyByNameRequired(projectManagersPolicyName);
        final var projectAdminsPolicy = authzService.findPolicyByNameRequired(projectAdminsPolicyName);

        final var viewPermissionPolicies = Set.of(stageViewersPolicy,
                stageManagersPolicy,
                stageAdminsPolicy,
                projectViewersPolicy,
                projectManagersPolicy,
                projectAdminsPolicy,
                tenantViewersPolicy,
                tenantManagersPolicy,
                tenantAdminsPolicy);
        deploymentAuthzService.createViewPermission(resourceId, versionResources, viewPermissionPolicies);

        final var managePermissionPolicies = Set.of(stageManagersPolicy,
                stageAdminsPolicy,
                projectManagersPolicy,
                projectAdminsPolicy,
                tenantManagersPolicy,
                tenantAdminsPolicy);
        deploymentAuthzService.createManagePermission(resourceId, versionResources, managePermissionPolicies);

        final var adminPermissionPolicies = Set.of(stageAdminsPolicy, projectAdminsPolicy, tenantAdminsPolicy);
        deploymentAuthzService.createAdminPermission(resourceId, versionResources, adminPermissionPolicies);

        deploymentService.switchStateFromCreatingToCreated(resourceId);
    }
}
