package com.crionuke.application;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class Resource {

    @GET
    @Path("/tenants/{id}")
    public String getById(@NotNull final Long id) {
        return "Tenant %d".formatted(id);
    }
}
