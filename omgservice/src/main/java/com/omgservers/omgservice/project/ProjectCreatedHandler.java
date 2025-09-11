package com.omgservers.omgservice.project;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectCreatedHandler implements EventHandler {

    final ProjectService projectService;

    public ProjectCreatedHandler(final ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.PROJECT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        projectService.switchStateFromCreatingToCreated(resourceId);
    }
}
