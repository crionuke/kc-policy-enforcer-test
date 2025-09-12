package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TenantCreatedHandler implements EventHandler {

    final TenantAuthzService tenantAuthzService;
    final TenantService tenantService;

    public TenantCreatedHandler(final TenantAuthzService tenantAuthzService,
                                final TenantService tenantService) {
        this.tenantAuthzService = tenantAuthzService;
        this.tenantService = tenantService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.TENANT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        tenantAuthzService.createResourceIfAny(resourceId);
        tenantAuthzService.createViewersGroupIfAny(resourceId);
        tenantAuthzService.createManagersGroupIfAny(resourceId);
        tenantAuthzService.createOwnersGroupIfAny(resourceId);
        tenantService.switchStateFromCreatingToCreated(resourceId);
    }
}
