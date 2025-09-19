package com.omgservers.omgservice.version;

import com.omgservers.omgservice.OidcClients;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventResourceClient;
import com.omgservers.omgservice.project.TestProjectService;
import com.omgservers.omgservice.tenant.TestTenantService;
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
@TestHTTPEndpoint(VersionResource.class)
public class VersionResourceTest extends Assertions {

    @Inject
    TestTenantService testTenantService;

    @Inject
    TestProjectService testProjectService;

    @Inject
    TestVersionService testVersionService;

    @Inject
    VersionResourceClient versionResourceClient;

    @Inject
    EventResourceClient eventResourceClient;

    @Inject
    OidcClients oidcClients;

    @Test
    void testGetVersionByIdSuccess() {
        final var token = oidcClients.getAdminAccessToken();

        final var testTenant = testTenantService.createTenant(true, token);
        final var testProject = testProjectService.createProject(testTenant.id, true, token);
        final var createdVersion = testVersionService.createVersion(testProject.id, true, token);

        final var versionById = versionResourceClient.getByIdCheck200(testProject.id, createdVersion.id, token);

        assertEquals(createdVersion.id, versionById.id);
        assertEquals(createdVersion.major, versionById.major);
        assertEquals(createdVersion.minor, versionById.minor);
        assertEquals(createdVersion.patch, versionById.patch);
        assertEquals(VersionStatus.CREATED, versionById.status);
    }

    @Test
    void testCreateVersionSuccess() {
        final var token = oidcClients.getAdminAccessToken();

        final var testTenant = testTenantService.createTenant(true, token);
        final var testProject = testProjectService.createProject(testTenant.id, true, token);

        final var newVersion = new NewVersion();
        newVersion.major = 1L;
        newVersion.minor = 2L;
        newVersion.patch = 3L;

        final var createdVersion = testVersionService.createVersion(testProject.id, newVersion, false, token);

        assertNotNull(createdVersion.id);
        assertEquals(newVersion.major, createdVersion.major);
        assertEquals(newVersion.minor, createdVersion.minor);
        assertEquals(newVersion.patch, createdVersion.patch);
        assertEquals(VersionStatus.CREATING, createdVersion.status);

        eventResourceClient.getByQualifierAndResourceIdCheck200(EventQualifier.VERSION_INSERTED,
                createdVersion.id,
                token);
    }

    @Test
    void testCreateVersionValidationFailed() {
        final var token = oidcClients.getAdminAccessToken();
        final var testTenant = testTenantService.createTenant(true, token);
        final var testProject = testProjectService.createProject(testTenant.id, true, token);
        final var newVersion = new NewVersion();
        final var errorResponse = versionResourceClient.createCheck4xx(testProject.id,
                newVersion,
                400,
                token);
        assertEquals("ValidationFailed", errorResponse.code);
    }
}