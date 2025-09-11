package com.omgservers.omgservice.errors;

public class TenantNotFound extends ResourceNotFound {

    public TenantNotFound(final Long tenantId) {
        super("Tenant %d not found"
                .formatted(tenantId));
    }
}
