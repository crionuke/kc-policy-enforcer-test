package com.omgservers.omgservice.project;

import com.omgservers.omgservice.OidcClients;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventResourceClient;
import com.omgservers.omgservice.tenant.TestTenantService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@QuarkusTest
@TestHTTPEndpoint(ProjectResource.class)
public class ProjectResourceTest extends Assertions {

    @Inject
    TestTenantService testTenantService;

    @Inject
    TestProjectService testProjectService;

    @Inject
    ProjectResourceClient projectResourceClient;

    @Inject
    EventResourceClient eventResourceClient;

    @Inject
    OidcClients oidcClients;

    @Test
    void testGetProjectByIdSuccess() {
        final var token = oidcClients.getAdminAccessToken();

        final var testTenant = testTenantService.createTenant(true, token);
        final var createdProject = testProjectService.createProject(testTenant.id, true, token);

        final var projectById = projectResourceClient.getByIdCheck200(createdProject.id, token);

        assertEquals(createdProject.id, projectById.id);
        assertEquals(createdProject.name, projectById.name);
        assertEquals(ProjectStatus.CREATED, projectById.status);
    }

    @Test
    void testCreateProjectSuccess() {
        final var token = oidcClients.getAdminAccessToken();

        final var testTenant = testTenantService.createTenant(true, token);

        final var newProject = new NewProject();
        newProject.name = "project-" + UUID.randomUUID();
        final var createdProject = testProjectService.createProject(testTenant.id, newProject, true, token);

        assertNotNull(createdProject.id);
        assertEquals(newProject.name, createdProject.name);
        assertEquals(ProjectStatus.CREATING, createdProject.status);

        eventResourceClient.getByQualifierAndResourceIdCheck200(EventQualifier.PROJECT_INSERTED,
                createdProject.id,
                token);
    }

    @Test
    void testCreateProjectValidationFailed() {
        final var token = oidcClients.getAdminAccessToken();
        final var testTenant = testTenantService.createTenant(true, token);
        final var newProject = new NewProject();
        final var errorResponse = projectResourceClient.createCheck4xx(testTenant.id,
                newProject,
                400,
                token);
        assertEquals("ValidationFailed", errorResponse.code);
    }
}