package com.omgservers.tenants.errors;

import com.omgservers.tenants.tenant.TenantStatus;

import java.util.UUID;

public class TenantStatusMismatch extends ResourceConflict {

    public TenantStatusMismatch(final UUID tenantId,
                                final TenantStatus current,
                                final TenantStatus required) {
        super("Tenant %s has invalid status. Current: %s, required: %s"
                .formatted(tenantId, current, required));
    }
}
