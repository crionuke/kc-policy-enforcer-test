package com.omgservers.tenants.tenant;

import jakarta.validation.constraints.NotNull;

public class TenantConfig {

    @NotNull
    public TenantConfigVersion version;
}
