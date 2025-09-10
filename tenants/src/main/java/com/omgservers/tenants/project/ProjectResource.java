package com.omgservers.tenants.project;

import com.omgservers.tenants.event.EventService;
import com.omgservers.tenants.tenant.Tenant;
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

@Path("/tenant/{tenantId}/project")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    final EventService eventService;

    public ProjectResource(final EventService eventService) {
        this.eventService = eventService;
    }

    @GET
    @Path("/{id}")
    public Project getById(@NotNull final Long id) {
        return Project.findByIdRequired(id);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    public Project create(@RestPath @NotNull final Long tenantId,
                          @NotNull @Valid final NewProject newProject) {
        final var tenant = Tenant.findByIdRequired(tenantId);
        tenant.ensureCreatedStatus();

        final var project = new Project();
        project.tenant = tenant;
        project.name = newProject.name;
        project.status = ProjectStatus.CREATING;
        project.config = newProject.config;
        project.persist();

        eventService.projectCreated(project.id);
        
        return project;
    }
}
