package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.OidcClients;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventResourceClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@QuarkusTest
@ApplicationScoped
@TestHTTPEndpoint(TenantResource.class)
public class TenantResourceTest extends Assertions {

    @Inject
    TenantResourceClient tenantResourceClient;

    @Inject
    TestTenantService testTenantService;

    @Inject
    EventResourceClient eventResourceClient;

    @Inject
    OidcClients oidcClients;

    @Test
    void testGetTenantByIdSuccess() {
        final var token = oidcClients.getAdminAccessToken();

        final var createdTenant = testTenantService.createTenant(true, token);

        final var tenantById = tenantResourceClient.getByIdCheck200(createdTenant.id, token);
        assertEquals(createdTenant.id, tenantById.id);
        assertEquals(createdTenant.name, tenantById.name);
        assertEquals(TenantStatus.CREATED, tenantById.status);
    }

    @Test
    void testCreateTenantSuccess() {
        final var token = oidcClients.getAdminAccessToken();

        final var newTenant = new NewTenant();
        newTenant.name = "tenant-" + UUID.randomUUID();

        final var createdTenant = testTenantService.createTenant(newTenant, false, token);

        assertNotNull(createdTenant.id);
        assertEquals(newTenant.name, createdTenant.name);
        assertEquals(TenantStatus.CREATING, createdTenant.status);

        eventResourceClient.getByQualifierAndResourceIdCheck200(EventQualifier.TENANT_INSERTED,
                createdTenant.id,
                token);
    }

    @Test
    void testCreateTenantValidationFailed() {
        final var token = oidcClients.getAdminAccessToken();
        final var newTenant = new NewTenant();
        final var errorResponse = tenantResourceClient.createCheck4xx(newTenant, 400, token);
        assertEquals("ValidationFailed", errorResponse.code);
    }
}