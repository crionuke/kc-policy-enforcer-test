package com.omgservers.tenants.deployment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class NewDeployment {

    @NotNull
    public Long versionId;

    @Valid
    @NotNull
    public DeploymentConfig config;
}
