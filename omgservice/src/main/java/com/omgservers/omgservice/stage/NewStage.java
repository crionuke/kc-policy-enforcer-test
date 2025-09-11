package com.omgservers.omgservice.stage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NewStage {

    @NotBlank
    public String name;

    @Valid
    @NotNull
    public StageConfig config;
}
