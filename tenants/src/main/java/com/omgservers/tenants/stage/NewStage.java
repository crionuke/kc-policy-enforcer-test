package com.omgservers.tenants.stage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class NewStage {

    @NotNull
    public UUID tenantId;

    @NotBlank
    public String name;

    @Valid
    @NotNull
    public StageConfig config;
}
