package com.omgservers.omgservice.project;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import com.omgservers.omgservice.tenant.Tenant;
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

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

    final EventService eventService;

    public ProjectResource(final EventService eventService) {
        this.eventService = eventService;
    }

    @GET
    @Path("/project/{id}")
    public Project getById(@NotNull final Long id) {
        return Project.findByIdRequired(id);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @Path("/tenant/{tenantId}/project")
    @Consumes(MediaType.APPLICATION_JSON)
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

        eventService.create(EventQualifier.PROJECT_CREATED, project.id);
        
        return project;
    }
}
