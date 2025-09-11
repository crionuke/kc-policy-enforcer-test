package com.omgservers.omgservice.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NewProject {

    @NotBlank
    public String name;

    @Valid
    @NotNull
    public ProjectConfig config;
}
