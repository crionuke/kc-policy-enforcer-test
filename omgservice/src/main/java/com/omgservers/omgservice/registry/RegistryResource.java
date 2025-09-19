package com.omgservers.omgservice.registry;

import jakarta.inject.Provider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class RegistryResource {

    final RegistryService registryService;
    final Provider<String> subClaim;

    public RegistryResource(final RegistryService registryService,
                            final @Claim(standard = Claims.sub) Provider<String> subClaim) {
        this.registryService = registryService;
        this.subClaim = subClaim;
    }

    @GET
    @Path("/projects/{projectId}/registries/{id}")
    public RegistryProjection getById(@PathParam("projectId") @NotNull final Long projectId,
                                      @PathParam("id") @NotNull final Long id) {
        return registryService.getById(projectId, id)
                .toProjection();
    }

    @POST
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{projectId}/registries")
    public RegistryProjection create(@PathParam("projectId") @NotNull final Long projectId,
                                     @NotNull @Valid final NewRegistry newRegistry) {
        return registryService.create(projectId, newRegistry, subClaim.get())
                .toProjection();
    }
}