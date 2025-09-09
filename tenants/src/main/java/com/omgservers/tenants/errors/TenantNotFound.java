package com.omgservers.tenants.errors;

import java.util.UUID;

public class TenantNotFound extends ResourceNotFound {

    public TenantNotFound(final UUID tenantId) {
        super("Tenant %s not found"
                .formatted(tenantId));
    }
}
