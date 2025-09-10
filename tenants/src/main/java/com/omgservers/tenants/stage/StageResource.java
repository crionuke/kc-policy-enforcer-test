package com.omgservers.tenants.stage;

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

@Path("/tenant/{tenantId}/stage")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StageResource {

    final EventService eventService;

    public StageResource(final EventService eventService) {
        this.eventService = eventService;
    }

    @GET
    @Path("/{id}")
    public Stage getById(@NotNull final Long id) {
        return Stage.findByIdRequired(id);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    public Stage create(@RestPath @NotNull final Long tenantId,
                        @NotNull @Valid final NewStage newStage) {
        final var tenant = Tenant.findByIdRequired(tenantId);
        tenant.ensureCreatedStatus();

        final var stage = new Stage();
        stage.tenant = tenant;
        stage.name = newStage.name;
        stage.status = StageStatus.CREATING;
        stage.config = newStage.config;
        stage.persist();

        eventService.stageCreated(stage.id);
        
        return stage;
    }
}
