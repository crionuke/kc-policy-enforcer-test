package com.omgservers.omgservice.project;

import com.omgservers.omgservice.authz.AuthzEntity;
import com.omgservers.omgservice.authz.KeycloakService;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.tenant.TenantAuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class ProjectCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCreatedHandler.class);

    final ProjectAuthzService projectAuthzService;
    final TenantAuthzService tenantAuthzService;
    final ProjectCreatedHandler thisHandler;
    final KeycloakService keycloakService;
    final ProjectService projectService;

    public ProjectCreatedHandler(final ProjectAuthzService projectAuthzService,
                                 final TenantAuthzService tenantAuthzService,
                                 final ProjectCreatedHandler thisHandler,
                                 final KeycloakService keycloakService,
                                 final ProjectService projectService) {
        this.projectAuthzService = projectAuthzService;
        this.tenantAuthzService = tenantAuthzService;
        this.thisHandler = thisHandler;
        this.projectService = projectService;
        this.keycloakService = keycloakService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.PROJECT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var project = Project.findByIdRequired(resourceId);

        LOGGER.info("Creating project {}", project);

        final var tenantId = project.tenant.id;
        final var createdBy = project.createdBy;

        final var authz = new ProjectConfig.Authz();

        final var viewersGroup = projectAuthzService.createViewersGroup(resourceId);
        authz.viewersGroup = new AuthzEntity(viewersGroup.getId(), viewersGroup.getName());

        final var managersGroup = projectAuthzService.createManagersGroup(resourceId);
        authz.managersGroup = new AuthzEntity(managersGroup.getId(), managersGroup.getName());

        final var adminsGroup = projectAuthzService.createAdminsGroup(resourceId);
        authz.adminsGroup = new AuthzEntity(adminsGroup.getId(), adminsGroup.getName());

        keycloakService.joinGroup(createdBy, adminsGroup);

        final var authzResource = projectAuthzService.createResource(tenantId, resourceId);
        authz.authzResource = new AuthzEntity(authzResource.getId(), authzResource.getName());

        final var viewersPolicy = projectAuthzService.createViewersPolicy(resourceId, viewersGroup);
        authz.viewersPolicy = new AuthzEntity(viewersPolicy.getId(), viewersPolicy.getName());

        final var managersPolicy = projectAuthzService.createManagersPolicy(resourceId, managersGroup);
        authz.managersPolicy = new AuthzEntity(managersPolicy.getId(), managersPolicy.getName());

        final var adminsPolicy = projectAuthzService.createAdminsPolicy(resourceId, adminsGroup);
        authz.adminsPolicy = new AuthzEntity(adminsPolicy.getId(), adminsPolicy.getName());

        final var tenantViewersPolicyName = project.tenant.config.authz.viewersPolicy.name;
        final var tenantManagersPolicyName = project.tenant.config.authz.managersPolicy.name;
        final var tenantAdminsPolicyName = project.tenant.config.authz.adminsPolicy.name;

        final var tenantViewersPolicy = keycloakService.findPolicyByNameRequired(tenantViewersPolicyName);
        final var tenantManagersPolicy = keycloakService.findPolicyByNameRequired(tenantManagersPolicyName);
        final var tenantAdminsPolicy = keycloakService.findPolicyByNameRequired(tenantAdminsPolicyName);

        final var viewPolicies = Set.of(viewersPolicy, managersPolicy, adminsPolicy, tenantViewersPolicy,
                tenantManagersPolicy, tenantAdminsPolicy);
        final var viewPermission = projectAuthzService.createViewPermission(resourceId, authzResource, viewPolicies);
        authz.viewPermission = new AuthzEntity(viewPermission.getId(), viewPermission.getName());

        final var managePolicies = Set.of(managersPolicy, adminsPolicy, tenantAdminsPolicy);
        final var managePermission = projectAuthzService.createManagePermission(resourceId, authzResource,
                managePolicies);
        authz.managePermission = new AuthzEntity(managePermission.getId(), managePermission.getName());

        final var adminPolicies = Set.of(adminsPolicy, tenantAdminsPolicy);
        final var adminPermission = projectAuthzService.createAdminPermission(resourceId, authzResource,
                adminPolicies);
        authz.adminPermission = new AuthzEntity(adminPermission.getId(), adminPermission.getName());

        thisHandler.finish(resourceId, authz);

        LOGGER.info("Project {} created successfully", project);
    }

    @Transactional
    public void finish(final Long projectId, final ProjectConfig.Authz authz) {
        final var project = Project.findByIdLocked(projectId);
        project.finishCreation(authz);
    }
}
