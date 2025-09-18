package com.omgservers.omgservice.project;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.TestEventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class TestProjectService {

    @Inject
    ProjectResourceClient projectResourceClient;

    @Inject
    TestEventService testEventService;

    public ProjectProjection createProject(final Long tenantId,
                                 final NewProject newProject,
                                 final boolean process,
                                 final String token) {
        final var project = projectResourceClient.createCheck201(tenantId, newProject, token);
        if (process) {
            testEventService.process(EventQualifier.PROJECT_CREATED, project.id);
        }

        return project;
    }

    public ProjectProjection createProject(final Long tenantId,
                                 final boolean process,
                                 final String token) {
        final var newProject = new NewProject();
        newProject.name = "project-" + UUID.randomUUID();
        return createProject(tenantId, newProject, process, token);
    }
}
