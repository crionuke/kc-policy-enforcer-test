package com.omgservers.omgservice.tenant;

import jakarta.inject.Provider;
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

    final TenantService tenantService;
    final Provider<String> subClaim;

    public TenantResource(final TenantService tenantService,
                          final @Claim(standard = Claims.sub) Provider<String> subClaim) {
        this.tenantService = tenantService;
        this.subClaim = subClaim;
    }

    @GET
    @Path("/tenant/{id}")
    public TenantProjection getById(@NotNull final Long id) {
        return tenantService.getById(id)
                .toProjection();
    }

    @POST
    @Path("/my/tenants")
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    public TenantProjection create(@NotNull @Valid final NewTenant newTenant) {
        return tenantService.create(newTenant, subClaim.get())
                .toProjection();
    }
}
