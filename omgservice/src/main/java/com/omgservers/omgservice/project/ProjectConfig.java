package com.omgservers.omgservice.project;

import jakarta.validation.constraints.NotNull;

public class ProjectConfig {

    @NotNull
    public ProjectConfigVersion version;
}
