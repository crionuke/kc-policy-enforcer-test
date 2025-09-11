package com.omgservers.omgservice.tenant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NewTenant {

    @NotBlank
    public String name;

    @Valid
    @NotNull
    public TenantConfig config;
}
