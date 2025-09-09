package com.omgservers.tenants.version;

import com.omgservers.tenants.event.Event;
import com.omgservers.tenants.event.EventQualifier;
import com.omgservers.tenants.project.Project;
import com.omgservers.tenants.project.ProjectResourceTest;
import com.omgservers.tenants.project.ProjectStatus;
import com.omgservers.tenants.tenant.TenantResourceTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@ApplicationScoped
@TestHTTPEndpoint(VersionResource.class)
public class VersionResourceTest {

    @Inject
    TenantResourceTest tenantResourceTest;

    @Inject
    ProjectResourceTest projectResourceTest;

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public Version persistTestVersion(final Project project) {
        final var testVersion = createTestVersion(project);
        testVersion.persist();
        return testVersion;
    }

    @Test
    void testGetVersionByIdSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);
        final var testVersion = persistTestVersion(testProject);

        given()
                .when()
                .get("/{id}", testVersion.id)
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testVersion.id.toString()))
                .body("project.id", equalTo(testProject.id.toString()))
                .body("major", equalTo(testVersion.major.intValue()))
                .body("minor", equalTo(testVersion.minor.intValue()))
                .body("patch", equalTo(testVersion.patch.intValue()))
                .body("status", equalTo(testVersion.status.toString()))
                .body("config", notNullValue());
    }

    @Test
    void testGetVersionByIdNotFound() {
        final var nonExistentId = UUID.randomUUID();

        given()
                .when()
                .get("/{id}", nonExistentId)
                .then()
                .log().body()
                .statusCode(404)
                .body("code", equalTo("VersionNotFound"));
    }

    @Test
    void testCreateVersionSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);

        final var newVersion = new NewVersion();
        newVersion.projectId = testProject.id;
        newVersion.major = 1L;
        newVersion.minor = 0L;
        newVersion.patch = 0L;
        newVersion.config = createVersionConfig();

        final var version = given()
                .contentType(ContentType.JSON)
                .body(newVersion)
                .when()
                .post()
                .then()
                .log().body()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("major", equalTo(newVersion.major.intValue()))
                .body("minor", equalTo(newVersion.minor.intValue()))
                .body("patch", equalTo(newVersion.patch.intValue()))
                .body("status", equalTo(VersionStatus.CREATING.toString()))
                .body("config", notNullValue())
                .extract().as(Version.class);

        Event.findFirstRequired(EventQualifier.VERSION_CREATED, version.id);
    }

    @Test
    void testCreateVersionValidationFailed() {
        final var invalidVersion = new NewVersion();

        given()
                .contentType(ContentType.JSON)
                .body(invalidVersion)
                .when()
                .post()
                .then()
                .log().body()
                .statusCode(400)
                .body("code", equalTo("ValidationFailed"));
    }

    @Test
    void testCreateVersionProjectStatusMismatch() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant, ProjectStatus.CREATING);

        final var newVersion = new NewVersion();
        newVersion.projectId = testProject.id;
        newVersion.major = 1L;
        newVersion.minor = 0L;
        newVersion.patch = 0L;
        newVersion.config = createVersionConfig();

        given()
                .contentType(ContentType.JSON)
                .body(newVersion)
                .when()
                .post()
                .then()
                .log().body()
                .statusCode(409)
                .body("code", equalTo("ProjectStatusMismatch"));
    }

    private Version createTestVersion(final Project project) {
        final var version = new Version();
        version.project = project;
        version.major = 1L;
        version.minor = 0L;
        version.patch = 0L;
        version.status = VersionStatus.CREATED;
        version.config = createVersionConfig();
        version.persist();

        return version;
    }

    private VersionConfig createVersionConfig() {
        final var config = new VersionConfig();
        config.version = VersionConfigVersion.V1;
        return config;
    }
}