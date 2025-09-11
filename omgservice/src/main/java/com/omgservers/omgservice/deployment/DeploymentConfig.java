package com.omgservers.omgservice.deployment;

import jakarta.validation.constraints.NotNull;

public class DeploymentConfig {

    @NotNull
    public DeploymentConfigVersion version;
}