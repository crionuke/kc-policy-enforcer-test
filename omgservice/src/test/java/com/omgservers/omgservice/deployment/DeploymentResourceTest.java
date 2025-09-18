package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.OidcClients;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventResourceClient;
import com.omgservers.omgservice.project.TestProjectService;
import com.omgservers.omgservice.stage.TestStageService;
import com.omgservers.omgservice.tenant.TestTenantService;
import com.omgservers.omgservice.version.TestVersionService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@ApplicationScoped
@TestSecurity(authorizationEnabled = false)
@TestHTTPEndpoint(DeploymentResource.class)
public class DeploymentResourceTest extends Assertions {

    @Inject
    TestTenantService testTenantService;

    @Inject
    TestProjectService testProjectService;

    @Inject
    TestVersionService testVersionService;

    @Inject
    TestStageService testStageService;

    @Inject
    TestDeploymentService testDeploymentService;

    @Inject
    DeploymentResourceClient deploymentResourceClient;

    @Inject
    EventResourceClient eventResourceClient;

    @Inject
    OidcClients oidcClients;

    @Test
    void testGetDeploymentByIdSuccess() {
        final var token = oidcClients.getAdminAccessToken();

        final var testTenant = testTenantService.createTenant(true, token);
        final var testProject = testProjectService.createProject(testTenant.id, true, token);
        final var testVersion = testVersionService.createVersion(testProject.id, true, token);
        final var testStage = testStageService.createStage(testTenant.id, true, token);
        final var createdDeployment = testDeploymentService
                .createDeployment(testStage.id, testVersion.id, true, token);

        final var deploymentById = deploymentResourceClient.getByIdCheck200(testStage.id, createdDeployment.id, token);

        assertEquals(createdDeployment.id, deploymentById.id);
        assertEquals(createdDeployment.versionId, deploymentById.versionId);
        assertEquals(DeploymentStatus.CREATED, deploymentById.status);
    }

    @Test
    void testCreateDeploymentSuccess() {
        final var token = oidcClients.getAdminAccessToken();

        final var testTenant = testTenantService.createTenant(true, token);
        final var testProject = testProjectService.createProject(testTenant.id, true, token);
        final var testVersion = testVersionService.createVersion(testProject.id, true, token);
        final var testStage = testStageService.createStage(testTenant.id, true, token);

        final var newDeployment = new NewDeployment();
        newDeployment.versionId = testVersion.id;

        final var createdDeployment = testDeploymentService
                .createDeployment(testStage.id, newDeployment, false, token);

        assertNotNull(createdDeployment.id);
        assertEquals(newDeployment.versionId, createdDeployment.versionId);
        assertEquals(DeploymentStatus.CREATING, createdDeployment.status);

        eventResourceClient.getByQualifierAndResourceIdCheck200(EventQualifier.DEPLOYMENT_CREATED,
                createdDeployment.id,
                token);
    }

    @Test
    void testCreateDeploymentValidationFailed() {
        final var token = oidcClients.getAdminAccessToken();
        final var testTenant = testTenantService.createTenant(true, token);
        final var testProject = testProjectService.createProject(testTenant.id, true, token);
        final var testVersion = testVersionService.createVersion(testProject.id, true, token);
        final var testStage = testStageService.createStage(testTenant.id, true, token);
        final var newDeployment = new NewDeployment();
        final var errorResponse = deploymentResourceClient.createCheck4xx(testStage.id,
                newDeployment,
                400,
                token);
        assertEquals("ValidationFailed", errorResponse.code);
    }
}