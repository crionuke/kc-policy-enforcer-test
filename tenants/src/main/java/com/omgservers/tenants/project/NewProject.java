package com.omgservers.tenants.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class NewProject {

    @NotNull
    public UUID tenantId;

    @NotBlank
    public String name;

    @Valid
    @NotNull
    public ProjectConfig config;
}
