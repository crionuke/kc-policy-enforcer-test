package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import com.omgservers.omgservice.tenant.Tenant;
import jakarta.inject.Provider;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestPath;

import java.util.UUID;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class StageResource {

    final EventService eventService;
    final Provider<String> subClaim;

    public StageResource(final EventService eventService,
                         final @Claim(standard = Claims.sub) Provider<String> subClaim) {
        this.eventService = eventService;
        this.subClaim = subClaim;
    }

    @GET
    @Path("/stage/{id}")
    public Stage getById(@NotNull final Long id) {
        return Stage.findByIdRequired(id);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @Path("/tenant/{tenantId}/stage")
    @Consumes(MediaType.APPLICATION_JSON)
    public Stage create(@RestPath @NotNull final Long tenantId,
                        @NotNull @Valid final NewStage newStage) {
        final var tenant = Tenant.findByIdRequired(tenantId);
        tenant.ensureCreatedStatus();

        final var stage = new Stage();
        stage.tenant = tenant;
        stage.name = newStage.name;
        stage.status = StageStatus.CREATING;
        stage.config = new StageConfig();
        stage.config.version = StageConfigVersion.V1;
        stage.config.createdBy = UUID.fromString(subClaim.get());
        stage.persist();

        eventService.create(EventQualifier.STAGE_CREATED, stage.id);
        
        return stage;
    }
}
