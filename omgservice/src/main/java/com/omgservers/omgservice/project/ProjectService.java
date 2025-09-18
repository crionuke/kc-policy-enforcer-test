package com.omgservers.omgservice.project;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import com.omgservers.omgservice.tenant.Tenant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class ProjectService {

    final EventService eventService;

    public ProjectService(final EventService eventService) {
        this.eventService = eventService;
    }

    public Project getById(final Long id) {
        return Project.findByIdRequired(id);
    }

    public Project create(final Long tenantId,
                          final NewProject newProject,
                          final String createdBy) {
        final var tenant = Tenant.findByIdRequired(tenantId);
        tenant.ensureCreatedStatus();

        final var project = new Project();
        project.createdBy = createdBy;
        project.tenant = tenant;
        project.name = newProject.name;
        project.status = ProjectStatus.CREATING;
        project.config = new ProjectConfig();
        project.config.version = ProjectConfigVersion.V1;
        project.persist();

        eventService.create(EventQualifier.PROJECT_CREATED, project.id);

        return project;
    }
}
