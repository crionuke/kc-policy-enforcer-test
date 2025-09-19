package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class TenantService {

    final EventService eventService;

    public TenantService(final EventService eventService) {
        this.eventService = eventService;
    }

    public Tenant getById(final Long id) {
        return Tenant.findByIdRequired(id);
    }

    public Tenant create(final NewTenant newTenant,
                         final String createdBy) {
        final var tenant = new Tenant();
        tenant.createdBy = createdBy;
        tenant.name = newTenant.name;
        tenant.status = TenantStatus.CREATING;
        tenant.config = new TenantConfig();
        tenant.config.version = TenantConfigVersion.V1;
        tenant.persist();

        eventService.create(EventQualifier.TENANT_INSERTED, tenant.id);

        return tenant;
    }
}
