package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.OidcClients;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventResourceClient;
import com.omgservers.omgservice.tenant.TestTenantService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@QuarkusTest
@ApplicationScoped
@TestHTTPEndpoint(StageResource.class)
public class StageResourceTest extends Assertions {

    @Inject
    TestTenantService testTenantService;

    @Inject
    TestStageService testStageService;

    @Inject
    StageResourceClient stageResourceClient;

    @Inject
    EventResourceClient eventResourceClient;

    @Inject
    OidcClients oidcClients;

    @Test
    void testGetStageByIdSuccess() {
        final var token = oidcClients.getAdminAccessToken();

        final var testTenant = testTenantService.createTenant(true, token);
        final var createdStage = testStageService.createStage(testTenant.id, true, token);

        final var stageById = stageResourceClient.getByIdCheck200(createdStage.id, token);

        assertEquals(createdStage.id, stageById.id);
        assertEquals(createdStage.name, stageById.name);
        assertEquals(StageStatus.CREATED, stageById.status);
    }

    @Test
    void testCreateStageSuccess() {
        final var token = oidcClients.getAdminAccessToken();

        final var testTenant = testTenantService.createTenant(true, token);

        final var newStage = new NewStage();
        newStage.name = "stage-" + UUID.randomUUID();
        final var createdStage = testStageService.createStage(testTenant.id, newStage, true, token);

        assertNotNull(createdStage.id);
        assertEquals(newStage.name, createdStage.name);
        assertEquals(StageStatus.CREATING, createdStage.status);

        eventResourceClient.getByQualifierAndResourceIdCheck200(EventQualifier.STAGE_INSERTED,
                createdStage.id,
                token);
    }

    @Test
    void testCreateStageValidationFailed() {
        final var token = oidcClients.getAdminAccessToken();
        final var testTenant = testTenantService.createTenant(true, token);
        final var newStage = new NewStage();
        final var errorResponse = stageResourceClient.createCheck4xx(testTenant.id,
                newStage,
                400,
                token);
        assertEquals("ValidationFailed", errorResponse.code);
    }
}