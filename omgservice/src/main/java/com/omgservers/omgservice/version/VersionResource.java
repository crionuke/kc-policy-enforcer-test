package com.omgservers.omgservice.version;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import com.omgservers.omgservice.project.Project;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestPath;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class VersionResource {

    final EventService eventService;

    public VersionResource(final EventService eventService) {
        this.eventService = eventService;
    }

    @GET
    @Path("/version/{id}")
    public Version getById(@NotNull final Long id) {
        return Version.findByIdRequired(id);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/project/{projectId}/version")
    public Version create(@RestPath @NotNull final Long projectId,
                          @NotNull @Valid final NewVersion newVersion) {
        final var project = Project.findByIdRequired(projectId);
        project.ensureCreatedStatus();

        final var version = new Version();
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