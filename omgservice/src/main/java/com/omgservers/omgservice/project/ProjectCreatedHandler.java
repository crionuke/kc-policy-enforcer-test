package com.omgservers.omgservice.project;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class ProjectCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCreatedHandler.class);

    final ProjectAuthzService projectAuthzService;
    final ProjectService projectService;

    public ProjectCreatedHandler(final ProjectAuthzService projectAuthzService,
                                 final ProjectService projectService) {
        this.projectAuthzService = projectAuthzService;
        this.projectService = projectService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.PROJECT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var project = Project.findByIdRequired(resourceId);

        LOGGER.info("Creating project {}", resourceId);

        final var viewersGroup = projectAuthzService.createViewersGroupIfAny(resourceId);
        final var developersGroup = projectAuthzService.createDevelopersGroupIfAny(resourceId);
        final var managersGroup = projectAuthzService.createManagersGroupIfAny(resourceId);
        final var adminsGroup = projectAuthzService.createAdminsGroupIfAny(resourceId);

        final var tenantId = project.tenant.id;
        final var resource = projectAuthzService.createResourceIfAny(tenantId, resourceId);

        final var viewersPolicy = projectAuthzService.createViewersPolicyIfAny(resourceId, viewersGroup);
        final var developersPolicy = projectAuthzService.createDevelopersPolicyIfAny(resourceId, developersGroup);
        final var managersPolicy = projectAuthzService.createManagersPolicyIfAny(resourceId, managersGroup);
        final var adminsPolicy = projectAuthzService.createAdminsPolicyIfAny(resourceId, adminsGroup);

        final var viewPermissionPolicies = Set.of(viewersPolicy, developersPolicy, managersPolicy, adminsPolicy);
        projectAuthzService.createViewPermissionIfAny(resourceId, resource, viewPermissionPolicies);
        projectAuthzService.createDevelopPermissionIfAny(resourceId, resource, Set.of(developersPolicy, adminsPolicy));
        projectAuthzService.createManagePermissionIfAny(resourceId, resource, Set.of(managersPolicy, adminsPolicy));
        projectAuthzService.createAdminPermissionIfAny(resourceId, resource, Set.of(adminsPolicy));

        projectService.switchStateFromCreatingToCreated(resourceId);
    }
}
