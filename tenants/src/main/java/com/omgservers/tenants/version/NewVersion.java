package com.omgservers.tenants.version;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public class NewVersion {

    @NotNull
    public UUID projectId;

    @NotNull
    @PositiveOrZero
    public Long major;

    @NotNull
    @PositiveOrZero
    public Long minor;

    @NotNull
    @PositiveOrZero
    public Long patch;

    @Valid
    @NotNull
    public VersionConfig config;
}