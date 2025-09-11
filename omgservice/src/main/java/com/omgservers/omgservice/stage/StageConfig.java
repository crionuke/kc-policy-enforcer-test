package com.omgservers.omgservice.stage;

import jakarta.validation.constraints.NotNull;

public class StageConfig {

    @NotNull
    public StageConfigVersion version;
}
