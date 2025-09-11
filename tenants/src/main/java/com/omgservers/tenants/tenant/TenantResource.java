package com.omgservers.tenants.tenant;

import com.omgservers.tenants.event.EventQualifier;
import com.omgservers.tenants.event.EventService;
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

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class TenantResource {

    final EventService eventService;

    public TenantResource(final EventService eventService) {
        this.eventService = eventService;
    }

    @GET
    @Path("/tenant/{id}")
    public Tenant getById(@NotNull final Long id) {
        return Tenant.findByIdRequired(id);
    }

    @POST
    @Transactional
    @Path("/tenant")
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    public Tenant create(@NotNull @Valid final NewTenant newTenant) {
        final var tenant = new Tenant();
        tenant.name = newTenant.name;
        tenant.status = TenantStatus.CREATING;
        tenant.config = newTenant.config;
        tenant.persist();

        eventService.create(EventQualifier.TENANT_CREATED, tenant.id);
        
        return tenant;
    }
}
