package com.omgservers.omgservice.tenant;

import jakarta.validation.constraints.NotNull;

public class TenantConfig {

    @NotNull
    public TenantConfigVersion version;
}
