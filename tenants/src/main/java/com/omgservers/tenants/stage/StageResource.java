package com.omgservers.tenants.stage;

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

import java.util.UUID;

@Path("/stage")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StageResource {

    @GET
    @Path("/{id}")
    public Stage getById(@NotNull final UUID id) {
        return Stage.findByIdRequired(id);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    public Stage create(@NotNull @Valid final NewStage newStage) {
        final var tenantId = newStage.tenantId;
        final var tenant = Tenant.findByIdRequired(tenantId);
        tenant.ensureCreatedStatus();

        final var stage = new Stage();
        stage.tenant = tenant;
        stage.name = newStage.name;
        stage.status = StageStatus.CREATING;
        stage.config = newStage.config;
        stage.persist();
        return stage;
    }
}
