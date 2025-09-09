package com.omgservers.tenants.tenant;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.UUID;

@Path("/tenant")
@Produces(MediaType.APPLICATION_JSON)
public class TenantResource {

    @GET
    @Path("/{id}")
    public Tenant getById(@NotNull final UUID id) {
        return Tenant.findByIdRequired(id);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    public Tenant create(@NotNull @Valid final NewTenant newTenant) {
        final var tenant = new Tenant();
        tenant.name = newTenant.name;
        tenant.status = TenantStatus.CREATING;
        tenant.config = newTenant.config;
        tenant.persist();
        return tenant;
    }
}
