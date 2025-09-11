package com.omgservers.omgservice.version;

import jakarta.validation.constraints.NotNull;

public class VersionConfig {

    @NotNull
    public VersionConfigVersion version;
}