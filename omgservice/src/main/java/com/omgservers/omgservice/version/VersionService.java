package com.omgservers.omgservice.version;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import com.omgservers.omgservice.project.Project;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class VersionService {

    final EventService eventService;

    public VersionService(final EventService eventService) {
        this.eventService = eventService;
    }

    public Version getById(final Long projectId, final Long id) {
        final var version = Version.findByIdRequired(id);
        version.ensureProject(projectId);
        return version;
    }

    public Version create(final Long projectId,
                          final NewVersion newVersion,
                          final String createdBy) {
        final var project = Project.findByIdRequired(projectId);
        project.ensureCreatedStatus();

        final var version = new Version();
        version.createdBy = createdBy;
        version.project = project;
        version.major = newVersion.major;
        version.minor = newVersion.minor;
        version.patch = newVersion.patch;
        version.status = VersionStatus.CREATING;
        version.config = new VersionConfig();
        version.config.version = VersionConfigVersion.V1;
        version.persist();

        eventService.create(EventQualifier.VERSION_CREATED, version.id);

        return version;
    }
}
