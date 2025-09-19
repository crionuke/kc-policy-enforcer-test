package com.omgservers.omgservice.handler;

import com.omgservers.omgservice.authz.AuthzEntity;
import com.omgservers.omgservice.authz.KeycloakService;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import com.omgservers.omgservice.project.Project;
import com.omgservers.omgservice.project.ProjectAuthzService;
import com.omgservers.omgservice.project.ProjectConfig;
import com.omgservers.omgservice.project.ProjectService;
import com.omgservers.omgservice.tenant.TenantAuthzService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class ProjectInserted implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectInserted.class);

    final ProjectAuthzService projectAuthzService;
    final TenantAuthzService tenantAuthzService;
    final ProjectInserted thisHandler;
    final KeycloakService keycloakService;
    final ProjectService projectService;
    final EventService eventService;

    public ProjectInserted(final ProjectAuthzService projectAuthzService,
                           final TenantAuthzService tenantAuthzService,
                           final ProjectInserted thisHandler,
                           final KeycloakService keycloakService,
                           final ProjectService projectService,
                           final EventService eventService) {
        this.projectAuthzService = projectAuthzService;
        this.tenantAuthzService = tenantAuthzService;
        this.keycloakService = keycloakService;
        this.projectService = projectService;
        this.eventService = eventService;
        this.thisHandler = thisHandler;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.PROJECT_INSERTED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var project = Project.findByIdRequired(resourceId);

        LOGGER.info("Creating {}", project);

        final var authz = createAuthz(project);
        thisHandler.finish(resourceId, authz);

        LOGGER.info("{} created successfully", project);
    }

    @Transactional
    public void finish(final Long projectId, final ProjectConfig.Authz authz) {
        final var project = Project.findByIdLocked(projectId);
        if (project.finishCreation(authz)) {
            eventService.create(EventQualifier.PROJECT_CREATED, projectId);
        }
    }

    ProjectConfig.Authz createAuthz(final Project project) {
        final var tenantId = project.tenant.id;
        final var projectId = project.id;
        final var createdBy = project.createdBy;

        final var authz = new ProjectConfig.Authz();

        final var viewersGroup = projectAuthzService.createViewersGroup(tenantId, projectId);
        authz.viewersGroup = new AuthzEntity(viewersGroup.getId(), viewersGroup.getName());

        final var managersGroup = projectAuthzService.createManagersGroup(tenantId, projectId);
        authz.managersGroup = new AuthzEntity(managersGroup.getId(), managersGroup.getName());

        final var adminsGroup = projectAuthzService.createAdminsGroup(tenantId, projectId);
        authz.adminsGroup = new AuthzEntity(adminsGroup.getId(), adminsGroup.getName());

        keycloakService.joinGroup(createdBy, adminsGroup);

        final var authzResource = projectAuthzService.createResource(tenantId, projectId);
        authz.authzResource = new AuthzEntity(authzResource.getId(), authzResource.getName());

        final var viewersPolicy = projectAuthzService.createViewersPolicy(projectId, viewersGroup);
        authz.viewersPolicy = new AuthzEntity(viewersPolicy.getId(), viewersPolicy.getName());

        final var managersPolicy = projectAuthzService.createManagersPolicy(projectId, managersGroup);
        authz.managersPolicy = new AuthzEntity(managersPolicy.getId(), managersPolicy.getName());

        final var adminsPolicy = projectAuthzService.createAdminsPolicy(projectId, adminsGroup);
        authz.adminsPolicy = new AuthzEntity(adminsPolicy.getId(), adminsPolicy.getName());

        final var tenantViewersPolicyName = project.tenant.config.authz.viewersPolicy.name;
        final var tenantManagersPolicyName = project.tenant.config.authz.managersPolicy.name;
        final var tenantAdminsPolicyName = project.tenant.config.authz.adminsPolicy.name;

        final var tenantViewersPolicy = keycloakService.findPolicyByNameRequired(tenantViewersPolicyName);
        final var tenantManagersPolicy = keycloakService.findPolicyByNameRequired(tenantManagersPolicyName);
        final var tenantAdminsPolicy = keycloakService.findPolicyByNameRequired(tenantAdminsPolicyName);

        final var viewPolicies = Set.of(viewersPolicy, managersPolicy, adminsPolicy, tenantViewersPolicy,
                tenantManagersPolicy, tenantAdminsPolicy);
        final var viewPermission = projectAuthzService.createViewPermission(projectId, authzResource, viewPolicies);
        authz.viewPermission = new AuthzEntity(viewPermission.getId(), viewPermission.getName());

        final var managePolicies = Set.of(managersPolicy, adminsPolicy, tenantAdminsPolicy);
        final var managePermission = projectAuthzService.createManagePermission(projectId, authzResource,
                managePolicies);
        authz.managePermission = new AuthzEntity(managePermission.getId(), managePermission.getName());

        final var adminPolicies = Set.of(adminsPolicy, tenantAdminsPolicy);
        final var adminPermission = projectAuthzService.createAdminPermission(projectId, authzResource,
                adminPolicies);
        authz.adminPermission = new AuthzEntity(adminPermission.getId(), adminPermission.getName());

        return authz;
    }
}
