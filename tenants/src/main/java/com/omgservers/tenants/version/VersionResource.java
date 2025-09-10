package com.omgservers.tenants.version;

import com.omgservers.tenants.event.EventService;
import com.omgservers.tenants.project.Project;
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

@Path("/tenant/{tenantId}/project/{projectId}/version")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VersionResource {

    final EventService eventService;

    public VersionResource(final EventService eventService) {
        this.eventService = eventService;
    }

    @GET
    @Path("/{id}")
    public Version getById(@NotNull final Long id) {
        return Version.findByIdRequired(id);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    public Version create(@RestPath @NotNull final Long tenantId,
                          @RestPath @NotNull final Long projectId,
                          @NotNull @Valid final NewVersion newVersion) {
        final var project = Project.findByIdRequired(projectId);
        project.ensureTenant(tenantId);
        project.ensureCreatedStatus();

        final var version = new Version();
        version.project = project;
        version.major = newVersion.major;
        version.minor = newVersion.minor;
        version.patch = newVersion.patch;
        version.status = VersionStatus.CREATING;
        version.config = newVersion.config;
        version.persist();

        eventService.versionCreated(version.id);

        return version;
    }
}