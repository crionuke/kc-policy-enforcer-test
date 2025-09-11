package com.omgservers.omgservice.tenant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class TenantService {

    public boolean switchStateFromCreatingToCreated(final Long tenantId) {
        final var tenant = Tenant.findByIdRequired(tenantId);
        if (tenant.status == TenantStatus.CREATING) {
            tenant.status = TenantStatus.CREATED;
            return true;
        } else {
            return false;
        }
    }
}
