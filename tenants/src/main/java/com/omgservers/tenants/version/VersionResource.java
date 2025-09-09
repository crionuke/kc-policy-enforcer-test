package com.omgservers.tenants.version;

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

import java.util.UUID;

@Path("/version")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VersionResource {

    @GET
    @Path("/{id}")
    public Version getById(@NotNull final UUID id) {
        return Version.findByIdRequired(id);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    public Version create(@NotNull @Valid final NewVersion newVersion) {
        final var projectId = newVersion.projectId;
        final var project = Project.findByIdRequired(projectId);
        project.ensureCreatedStatus();

        final var version = new Version();
        version.project = project;
        version.major = newVersion.major;
        version.minor = newVersion.minor;
        version.patch = newVersion.patch;
        version.status = VersionStatus.CREATING;
        version.config = newVersion.config;
        version.persist();
        return version;
    }
}