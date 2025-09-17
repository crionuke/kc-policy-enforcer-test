package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
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

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class TenantResource {

    final EventService eventService;
    final Provider<String> subClaim;

    public TenantResource(final EventService eventService,
                          final @Claim(standard = Claims.sub) Provider<String> subClaim) {
        this.eventService = eventService;
        this.subClaim = subClaim;
    }

    @GET
    @Path("/tenant/{id}")
    public Tenant getById(@NotNull final Long id) {
        return Tenant.findByIdRequired(id);
    }

    @POST
    @Transactional
    @Path("/my/tenants")
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    public Tenant create(@NotNull @Valid final NewTenant newTenant) {
        final var tenant = new Tenant();
        tenant.createdBy = subClaim.get();
        tenant.name = newTenant.name;
        tenant.status = TenantStatus.CREATING;
        tenant.config = new TenantConfig();
        tenant.config.version = TenantConfigVersion.V1;
        tenant.persist();

        eventService.create(EventQualifier.TENANT_CREATED, tenant.id);
        
        return tenant;
    }
}
