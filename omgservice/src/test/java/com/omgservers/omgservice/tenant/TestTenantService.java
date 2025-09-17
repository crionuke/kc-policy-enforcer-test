package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.TestEventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class TestTenantService {

    @Inject
    TenantResourceClient tenantResourceClient;

    @Inject
    TestEventService testEventService;

    public Tenant createTenant(final NewTenant newTenant,
                               final boolean process,
                               final String token) {
        final var createdTenant = tenantResourceClient.createCheck201(newTenant, token);
        if (process) {
            testEventService.process(EventQualifier.TENANT_CREATED, createdTenant.id);
        }

        return createdTenant;
    }

    public Tenant createTenant(final boolean process, final String token) {
        final var newTenant = new NewTenant();
        newTenant.name = "tenant-" + UUID.randomUUID();
        return createTenant(newTenant, process, token);
    }
}
