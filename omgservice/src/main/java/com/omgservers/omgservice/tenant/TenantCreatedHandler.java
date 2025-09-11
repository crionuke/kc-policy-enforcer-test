package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TenantCreatedHandler implements EventHandler {

    final TenantService tenantService;

    public TenantCreatedHandler(final TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.TENANT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        tenantService.switchStateFromCreatingToCreated(resourceId);
    }
}
