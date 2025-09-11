package com.omgservers.omgservice.project;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class ProjectService {

    public boolean switchStateFromCreatingToCreated(final Long projectId) {
        final var project = Project.findByIdRequired(projectId);
        if (project.status == ProjectStatus.CREATING) {
            project.status = ProjectStatus.CREATED;
            return true;
        } else {
            return false;
        }
    }
}
